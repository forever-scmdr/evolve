package ecommander.persistence.commandunits;

import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.mappers.LuceneIndexMapper;

/**
 * Удаление записей всех ранее удаленных айтемов. Происходит пакетами с заданным размером.
 * Также возмжно инофрмирование о количестве удаленных айтемов с помощью специального интерфейса
 * @author EEEE
 */
@Deprecated
public class CleanAllDeletedItemsDBUnit extends DBPersistenceCommandUnit {

	public interface DeleteInformer {
		void receiveDeletedCount(int deletedCount);
	}
	
	private int deleteBatchQty;
	private DeleteInformer informer;

	public CleanAllDeletedItemsDBUnit(int batchQty, DeleteInformer informer) {
		this.deleteBatchQty = batchQty;
		this.informer = informer;
	}

	public void execute() throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(context.getInitiator());
		int deletedCount;
		try {
			if (insertIntoFulltextIndex)
				LuceneIndexMapper.getSingleton().startUpdate();
			do {
				CleanDeletedItemsDBUnit cleanBatch = new CleanDeletedItemsDBUnit(deleteBatchQty);
				if (!insertIntoFulltextIndex)
					cleanBatch.noFulltextIndex();
				transaction.addCommandUnit(cleanBatch);
				transaction.execute();
				deletedCount = cleanBatch.getDeletedCount();
				if (informer != null) {
					informer.receiveDeletedCount(deletedCount);
				}
			} while (deletedCount > 0);
		} finally {
			if (insertIntoFulltextIndex)
				LuceneIndexMapper.getSingleton().finishUpdate();
		}
	}

}