from sqlalchemy import create_engine, MetaData, Table
from sqlalchemy.orm import sessionmaker
from sqlalchemy.exc import SQLAlchemyError
import pandas as pd

DATABASE_URI = 'postgresql://postgres:root@localhost:5432/authorization'

# Establish the connection to the database
engine = create_engine(DATABASE_URI)

metadata = MetaData()
metadata.reflect(bind=engine)

category_table = metadata.tables['category']

Session = sessionmaker(bind=engine)
session = Session()

df = pd.read_csv('../common_exercises.csv')

# Assuming 'equipment_details' is the column with the URLs and 'Equipment_x' is the column with the equipment names
unique_category= df[['Type']].drop_duplicates()
# unique_equipment = unique_equipment.dropna(subset=['Type'])
print(unique_category)

try:
    for index, category in unique_category.iterrows():
        # Check if the equipment already exists to avoid duplicates
        exists = session.query(category_table).filter_by(name=category['Type']).first() is not None
        if not exists:
            # Construct an insert statement for the equipment_table
            ins = category_table.insert().values(
                name=category['Type'], 
            )
            # Execute the statement
            session.execute(ins)
    # Commit the session if all inserts are successful
    session.commit()
    print("Data inserted successfully.")
except SQLAlchemyError as e:
    print(f"An error occurred while inserting data: {e}")
    session.rollback()  # Roll back the session on error
finally:
    session.close()