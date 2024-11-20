# Первая функция (создает строку из родительских ID)

DELIMITER $$

DROP FUNCTION IF EXISTS `spas`.`patch_one_item` $$
CREATE FUNCTION `spas`.`patch_one_item` (item_id BIGINT) RETURNS VARCHAR(255)
BEGIN
  DECLARE id BIGINT;
  DECLARE result VARCHAR(255) DEFAULT '';
  DECLARE done INT DEFAULT 0;
  DECLARE cur CURSOR FOR SELECT IP_PARENT_ID FROM ItemParent WHERE IP_ITEM_ID = item_id AND IP_REF_ID = item_id ORDER BY IP_LEVEL DESC;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

  OPEN cur;

  REPEAT
    FETCH cur INTO id;
    IF NOT done THEN
       SET result = CONCAT(result, id, '/');
    END IF;
  UNTIL done END REPEAT;

  CLOSE cur;

  RETURN result;
END $$

DELIMITER ;

# Вторая функция (меняет PATH во всех айтемах)
DELIMITER $$

DROP PROCEDURE IF EXISTS `patch_files` $$
CREATE PROCEDURE `patch_files`()
BEGIN
  UPDATE Item SET I_PRED_ID_PATH = patch_one_item(I_ID);
END $$

DELIMITER ;

# Удаление пути к файлу в значении параметра
SET '1/2/3/4/ddd.bmp' = RIGHT('1/2/3/4/ddd.bmp', INSTR(REVERSE('1/2/3/4/ddd.bmp'), '/') - 1)