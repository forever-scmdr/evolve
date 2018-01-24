package ecommander.persistence.commandunits;

import ecommander.persistence.mappers.LuceneIndexMapper;

/**
 * Удаление записей всех ранее удаленных айтемов. Происходит пакетами с заданным размером.
 * Также возмжно инофрмирование о количестве удаленных айтемов с помощью специального интерфейса
 * @author EEEE
 */
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
		int deletedCount;
		try {
			LuceneIndexMapper.getSingleton().startUpdate();
			do {
				CleanDeletedItemsDBUnit cleanBatch = new CleanDeletedItemsDBUnit(deleteBatchQty);
				executeCommand(cleanBatch);
				deletedCount = cleanBatch.getDeletedCount();
				if (informer != null) {
					informer.receiveDeletedCount(deletedCount);
				}
			} while (deletedCount > 0);
		} finally {
			LuceneIndexMapper.getSingleton().finishUpdate();
		}
	}

}