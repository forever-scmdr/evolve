package extra.belchip;

import ecommander.controllers.PageController;
import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.pages.*;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Preorder;
import extra._generated.Product;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreorderCommand extends Command {
	private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

	private static final Object MUTEX = new Object();
	private static volatile boolean isInProgress = false;
	private static volatile Info info = new Info();
	private HashSet<String> codes = new HashSet<>();
	private List<Item> customers = new ArrayList<>();

	@Override
	public ResultPE execute() throws Exception {
		String op = getVarSingleValue("action");
		if (isInProgress || StringUtils.isEmpty(op) || !op.equals("start")) {
			return buildResult();
		} else {
			synchronized (MUTEX) {
				isInProgress = true;
				info = new Info();
				try {
					Thread integrationThread = new Thread(new Runnable() {
						@Override
						public void run() {
							info.SetOperation("Формирование выборки");
							LogMessage m = info.addLog("Загрузка покупателей");

							try {
								ItemQuery q = new ItemQuery(ItemNames.PREORDER);
								long start = System.nanoTime();
								customers = q.loadItems();
								long elapsed = (System.nanoTime() - start) / 1000;
								m.elapsedTime = elapsed;

								for (Item cust : customers) {
									codes.addAll(cust.outputValues(Preorder.CODE));
								}

								LogMessage m1 = info.addLog("Загрузка поступивших товаров");
								q = new ItemQuery(ItemNames.PRODUCT);
								ArrayList<String> z = new ArrayList<String>();
								z.addAll(codes);
								q.addParameterCriteria(Product.CODE, z, "=", null, Compare.SOME.SOME);
								q.addParameterCriteria(Product.QTY, "0", ">", null, Compare.SOME.SOME);

								start = System.nanoTime();
								List<Item> products = q.loadItems();
								elapsed = (System.nanoTime() - start) / 1000;
								m1.elapsedTime = elapsed;

								info.addLog(String.format("Поступило товаров: %d", products.size()));

								codes = new HashSet<String>();
								for (Item product : products) {
									codes.add(product.getStringValue(Product.CODE));
								}

								if (customers.size() > 0) {

									ListIterator<Item> itr = customers.listIterator();

									info.SetOperation("Отправка писем");
									while (itr.hasNext()) {
										Item customer = itr.next();
										boolean contains = false;
										ArrayList<String> c = customer.outputValues(Preorder.CODE);
										for (String s : c) {
											if (codes.contains(s)) {
												contains = true;
												break;
											}
										}
										if (contains) {
											boolean succ = sendMailToCustomer(customer);
											if (succ)
												clearCodes(customer);
										}
									}

								}
								info.finish();
							} catch (Exception e) {
								ServerLogger.error(e.getMessage(), e);
								info.addError(e);
							}
						}

						private void clearCodes(Item customer) throws Exception {
							ArrayList<String> values = customer.outputValues(Preorder.CODE);
							for (String code : values) {
								if (codes.contains(code)) {
									customer.removeEqualValue(Preorder.CODE, code);
								}
							}
							values = customer.outputValues(Preorder.CODE);
							LogMessage msg = info.addLog("Очистка лишних данных");
							long start = System.nanoTime();
							if (values.size() == 0) {
								executeAndCommitCommandUnits(ItemStatusDBUnit.delete(customer).ignoreUser(true).noFulltextIndex());
							} else {
								executeAndCommitCommandUnits(SaveItemDBUnit.get(customer).ignoreUser(true).noFulltextIndex());
							}
							long elapsed = (System.nanoTime() - start) / 1000;
							msg.elapsedTime = elapsed;

						}

						private boolean sendMailToCustomer(Item customer) {
							ByteArrayOutputStream pageOutput = new ByteArrayOutputStream();
							ExecutablePagePE regularTemplate = null;

							// Prepairing page
							try {
								LogMessage msg = info.addLog("Формирование страницы");
								long start = System.nanoTime();
								LinkPE regularLink = LinkPE.newDirectLink("link", "wait_email", false);
								regularLink.addStaticVariable("sel", String.valueOf(customer.getId()));
								regularTemplate = getExecutablePage(regularLink.serialize());
								PageController.newSimple().executePage(regularTemplate, pageOutput);
								msg.elapsedTime = (System.nanoTime() - start) / 1000;
							} catch (Exception e) {
								ServerLogger.error(e);
								info.addError(e, "при формировании страницы со списком товаров.");
								return false;
							}

							// Отправка письма
							try {
								String email = customer.getStringValue(Preorder.EMAIL);
								LogMessage msg = info.addLog("Формирование тела письма: " + email);
								long start = System.nanoTime();
								Multipart regularMP = new MimeMultipart();
								MimeBodyPart regularTextPart = new MimeBodyPart();
								regularMP.addBodyPart(regularTextPart);
								regularTextPart.setContent(pageOutput.toString("UTF-8"),
										regularTemplate.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER) + ";charset=UTF-8");
								msg.elapsedTime = (System.nanoTime() - start) / 1000;

								LogMessage msg1 = info.addLog("Отправка письма: " + email);
								EmailUtils.sendGmailDefault(email.trim(), "Уведомление о поступлении товаров", regularMP);
								msg1.elapsedTime = (System.nanoTime() - start) / 1000;

							} catch (Exception e) {
								ServerLogger.error(e);
								info.addError(e, "при отправке письма.");
								return false;
							}
							return true;
						}
					});

					integrationThread.setName("Belchip email sending");
					integrationThread.start();

				} catch (Exception e) {
					ServerLogger.error(e);
					info.addError(e);
				} finally {
					isInProgress = false;
				}
			}
		}
		return buildResult();
	}

	public ResultPE add() throws EcommanderException {
		try {
			String email = getVarSingleValue("email");
			String code = getVarSingleValue("code");

			if (StringUtils.isBlank(email)) {
				ResultPE err = getResult("error");
				err.setVariable("message", "введите email");
				return err;
			}

			Pattern p = Pattern.compile(EMAIL_REGEX);
			Matcher m = p.matcher(email);
			if (!m.matches()) {
				ResultPE err = getResult("error");
				err.setVariable("message", "введен некорректный email");
				return err;
			}

			Item order = ItemQuery.loadSingleItemByParamValue(ItemNames.PREORDER, Preorder.EMAIL, email);
			if (order == null) {
				Item orders = ItemUtils.ensureSingleRootItem(ItemNames.PREORDERS, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.getDefaultUser().getUserId());
				order = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.PREORDER), orders);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(order).ignoreUser(true).noFulltextIndex());
			}

			ArrayList<String> vals = order.outputValues(Preorder.CODE);
			if (!vals.contains(code)) {
				order.setValue(Preorder.CODE, code);
				order.setValue(Preorder.EMAIL, email);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(order).ignoreUser(true).noFulltextIndex());
			}

			return getResult("device_added");
		} catch (Exception e) {
			return getResult("error");
		}
	}

	private ResultPE buildResult() throws IOException {
		XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
		doc.startElement("page");
		info.output(doc);
		doc.endElement();
		ResultPE result = null;
		try {
			result = getResult("success");
		} catch (EcommanderException e) {
			ServerLogger.error("no result found", e);
			return null;
		}
		result.setValue(doc.toString());
		return result;
	}

	private static class Info {
		private static final Format TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
		private ArrayList<LogMessage> log = new ArrayList<LogMessage>();
		private ArrayList<Error> errors = new ArrayList<Error>();

		private boolean refresh = true;
		private String currentOperation = "инициализация";
		private int emailsToProcess = 0;
		private int processed = 0;

		private synchronized void output(XmlDocumentBuilder doc) throws IOException {
			if (refresh) {
				doc.startElement("refresh").endElement();
			}
			doc.startElement("operation").addText(currentOperation).endElement();
			doc.startElement("processed").addText(processed).endElement();
			doc.startElement("to_process").addText(emailsToProcess).endElement();

			if (errors.size() != 0) {
//				doc.startElement("message").addText("Во время интеграции произошли ошибки. Процесс интеграции был прерван.").endElement();
//				doc.startElement("message").addText("Список ошибок:").endElement();
				for (Error e : errors) {
					doc.startElement("error", "time", TIME_FORMAT.format(e.date)).startElement("exception").addText(e.message).endElement();
					if (e.explain.length > 0) {
						for (String msg : e.explain) {
							doc.startElement("msg").addText(msg).endElement();
						}
					}
					doc.endElement();
				}
			}
			for (LogMessage msg : log) {
				doc.startElement("message", "time", TIME_FORMAT.format(msg.date), "elapsed", msg.elapsedTime).addText(msg.message)
						.endElement();
			}
		}

		public synchronized int getErrorsCount() {
			return errors.size();
		}

		private synchronized void finish() {
			emailsToProcess = 0;
			currentOperation = "Рассылка завершена.";
			refresh = false;
		}

		private synchronized void setToProcess(int toProcess) {
			emailsToProcess = toProcess;
		}

		private synchronized void incrementProcessed() {
			processed++;
		}

		private synchronized LogMessage addLog(String message) {
			LogMessage msg = new LogMessage(message);
			log.add(msg);
			return msg;
		}

		private synchronized void SetOperation(String operation) {
			this.currentOperation = operation;
		}

		private synchronized void addError(Exception e, String... message) {
			Error err = new Error(e, message);
			errors.add(err);
		}
	}

	private static class Error {
		private Date date;
		private String message;
		private String[] explain;

		private Error(Exception e, String... explain) {
			this.date = new Date();
			//this.message = e != null ? ExceptionUtils.getExceptionStackTrace(e) : "";
			this.message = e.toString();
			this.explain = explain;
		}
	}

	private static class LogMessage {
		private Date date;
		private long elapsedTime = 0;
		private String message;

		private LogMessage(String message) {
			this.date = new Date();
			this.message = message;
		}
	}
}
