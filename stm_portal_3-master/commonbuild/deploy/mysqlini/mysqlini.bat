del /f /s /q D:\bak\dbscript\*.*
svn export --force http://172.16.7.149/svn/OC/Doc/design/PORTAL/设计文档/数据库/dbscript   D:\bak\dbscript --username admin --password admin123
svn export --force http://172.16.7.149/svn/OC/Doc/dbscript/mysql_ini.sql   D:\bak\dbscript --username admin --password admin123

mysqldump  --default-character-set=utf8 -h172.16.7.143 -uroot -ppassword123! -d oc4db > D:\bak\dbscript\oc4db_all.sql
mysqldump  --default-character-set=utf8 -h172.16.7.143 -uroot -ppassword123! -t oc4_int oc_node oc_node_group > D:\bak\dbscript\oc4_int_ini.sql

mysql --default-character-set=utf8 -h172.16.7.143 -uroot -ppassword123! oc4_int < D:\bak\dbscript\oc4db_all.sql
mysql --default-character-set=utf8 -h172.16.7.143 -uroot -ppassword123! oc4_int < D:\bak\dbscript\OC4.SCHEMA.SQL
mysql --default-character-set=utf8 -h172.16.7.143 -uroot -ppassword123! oc4_int < D:\bak\dbscript\OC4.DATA.SQL
mysql --default-character-set=utf8 -h172.16.7.143 -uroot -ppassword123! oc4_int < D:\bak\dbscript\oc4_int_ini.sql
mysql --default-character-set=utf8 -h172.16.7.143 -uroot -ppassword123! oc4_int < D:\bak\dbscript\mysql_ini.sql

pause;