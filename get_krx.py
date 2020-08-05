import pymysql
from sqlalchemy import create_engine
import FinanceDataReader as fdr

# KRX 조회
df_krx = fdr.StockListing('KRX')
df_krx.head()

# DB
engine = create_engine("mysql+pymysql://root:hsjang11@localhost:3306/stocks?charset=utf8", encoding='utf-8')
df_krx.to_sql(name='krx', con=engine, if_exists='replace')
