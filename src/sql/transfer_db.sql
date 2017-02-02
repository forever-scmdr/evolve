# Transfer same tables (without modifications)

INSERT INTO db_dest.UserGroup (UG_NAME) 
SELECT UG_NAME FROM db_src.UserGroup;

INSERT INTO db_dest.Users (U_ID, U_GROUP, U_LOGIN, U_PASSWORD, U_DESCRIPTION) 
SELECT U_ID, U_GROUP, U_LOGIN, U_PASSWORD, U_DESCRIPTION FROM db_src.Users;

# Transfer ID tables

INSERT INTO db_dest.ItemIds (IID_ID, IID_NAME)
SELECT IA_TYPE_ID, IA_TYPE_NAME FROM db_src.ItemAbstract;

INSERT INTO db_dest.ParamIds (PID_PARAM_ID, PID_ITEM_ID, PID_PARAM_NAME)
SELECT IRA_PARAM_ID, IRA_TYPE_ID, IRA_PARAM_NAME FROM db_src.ItemParameterAbstract;

# Transfer data

# Item
INSERT INTO db_dest.Item
(I_ID, I_TYPE_ID, I_KEY, I_T_KEY, I_PARENT_ID, I_PRED_ID_PATH, I_REF_ID, I_WEIGHT, I_OWNER_GROUP_ID, I_OWNER_USER_ID, I_PARAMS)
SELECT I_ID, I_TYPE_ID, I_KEY, I_T_KEY, I_PARENT_ID, I_PRED_ID_PATH, I_REF_ID, I_WEIGHT, I_OWNER_GROUP_ID, I_OWNER_USER_ID, I_PARAMS
FROM db_src.Item;

# transfer root names (now they are located in I_KEY)
UPDATE db_dest.Item AS D, db_src.Item AS S SET D.I_KEY = S.I_TYPE_NAME WHERE S.I_TYPE_ID = 0 AND S.I_ID = D.I_ID;

# Delete reference items that for some reason have no real items (which are referenced by them)
DELETE FROM db_dest.Item WHERE I_REF_ID NOT IN (SELECT I_ID FROM db_src.Item) AND I_TYPE_ID != 0;

# Parents (same data)
INSERT INTO db_dest.ItemParent (IP_ITEM_ID, IP_PARENT_ID, IP_LEVEL, IP_REF_ID, IP_TYPE)
SELECT IP_ITEM_ID, IP_PARENT_ID, IP_LEVEL, IP_REF_ID, I_TYPE_ID FROM db_src.ItemParent, db_src.Item 
WHERE I_ID = IP_ITEM_ID;

# Parents (item as a parent of itself)
INSERT INTO db_dest.ItemParent (IP_ITEM_ID, IP_PARENT_ID, IP_LEVEL, IP_REF_ID, IP_TYPE)
SELECT I_ID, I_ID, 0, I_REF_ID, I_TYPE_ID FROM db_dest.Item WHERE I_PARENT_ID != 0;

# Double index
INSERT INTO db_dest.DoubleIndex (II_REF_ID, II_PARAM, II_TYPE, II_VAL, II_PARENT)
SELECT II_REF_ID, II_PARAM, II_TYPE, II_VAL, I_PARENT_ID FROM db_src.DoubleIndex, db_src.Item
WHERE II_REF_ID = I_ID;

# Integer index
INSERT INTO db_dest.IntIndex (II_REF_ID, II_PARAM, II_TYPE, II_VAL, II_PARENT)
SELECT II_REF_ID, II_PARAM, II_TYPE, II_VAL, I_PARENT_ID FROM db_src.IntIndex, db_src.Item
WHERE II_REF_ID = I_ID;

# String index
INSERT INTO db_dest.StringIndex (II_REF_ID, II_PARAM, II_TYPE, II_VAL, II_PARENT)
SELECT II_REF_ID, II_PARAM, II_TYPE, II_VAL, I_PARENT_ID FROM db_src.StringIndex, db_src.Item
WHERE II_REF_ID = I_ID;