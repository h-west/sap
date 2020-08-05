import pymysql
from sqlalchemy import create_engine
import FinanceDataReader as fdr
import mysql.connector

engine = create_engine("mysql+pymysql://root:hsjang11@localhost:3306/stocks?charset=utf8", encoding='utf-8')
result = engine.execute("SELECT * FROM krx").fetchall()
for x in result:
  ss = fdr.DataReader(x[1], '2015-06-01')
  ss['Symbol'] = [x[1]] * len(ss)
  ss.to_sql(name='series', con=engine, if_exists='append')
