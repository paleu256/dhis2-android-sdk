ALTER TABLE SectionGreyedFieldsLink RENAME TO SectionGreyedFieldsLink_old;
CREATE TABLE SectionGreyedFieldsLink (_id INTEGER PRIMARY KEY AUTOINCREMENT, section TEXT NOT NULL, dataElementOperand TEXT NOT NULL, categoryOptionCombo TEXT, FOREIGN KEY (section) REFERENCES Section (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (dataElementOperand) REFERENCES DataElementOperand (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (categoryOptionCombo) REFERENCES CategoryOptionCombo (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (section, dataElementOperand, categoryOptionCombo));
INSERT INTO SectionGreyedFieldsLink (_id, section, dataElementOperand) SELECT _id, section, dataElementOperand FROM SectionGreyedFieldsLink_old;
DROP TABLE IF EXISTS SectionGreyedFieldsLink_old;