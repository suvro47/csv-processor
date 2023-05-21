#CSV PROCESSOR


```check list```

step 1 : import csv data into a temporary table

step 2 : prepare input csv
         * add a new column school_id
           alter table BUILDING_IMAGE add school_code varchar2(256);

step 4 : populate school_id column
         CREATE INDEX idx_school_emis_code ON SCHOOL (EMIS_CODE);
         update BUILDING_IMAGE bi set bi.school_id = (select s.SCHOOL_ID from SCHOOL s where s.EMIS_CODE = bi.EP_SCHOOL_CODE);
         DROP INDEX idx_school_emis_code;

step 5 : export input csv (maintain the column sequence)
         select bi.SCHOOL_ID, bi.EP_SCHOOL_CODE, bi.VIEWNAME, bi.FILENAME
         from SCHOOL s join BUILDING_IMAGE bi on s.EMIS_CODE = bi.EP_SCHOOL_CODE
             where bi.SCHOOL_ID is not null and bi.EP_SCHOOL_CODE is not null
             and bi.FILENAME is not null and bi.VIEWNAME is not null;

step 6 : populate serial_no column in case of building and washblock
         select spg.SCHOOL_ID, spg.PHOTO_TYPE, spg.PHOTO_PATH,
                ROW_NUMBER() over (partition by spg.SCHOOL_ID, spg.PHOTO_TYPE order by SCHOOL_ID) - 1 as serial_no
         from SCHOOL_PHOTO_GALLERY spg;
         




