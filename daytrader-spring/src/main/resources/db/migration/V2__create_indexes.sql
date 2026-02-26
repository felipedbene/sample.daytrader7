CREATE INDEX idx_account_userid ON accountejb(PROFILE_USERID);
CREATE INDEX idx_holding_accountid ON holdingejb(ACCOUNT_ACCOUNTID);
CREATE INDEX idx_order_accountid ON orderejb(ACCOUNT_ACCOUNTID);
CREATE INDEX idx_order_holdingid ON orderejb(HOLDING_HOLDINGID);
CREATE INDEX idx_order_status_account ON orderejb(ACCOUNT_ACCOUNTID, ORDERSTATUS);
