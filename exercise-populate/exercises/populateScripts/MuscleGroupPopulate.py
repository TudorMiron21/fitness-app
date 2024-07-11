from sqlalchemy import create_engine, MetaData, Table
from sqlalchemy.orm import sessionmaker
from sqlalchemy.exc import SQLAlchemyError
import pandas as pd

DATABASE_URI = 'postgresql://postgres:root@localhost:5433/authorization'

# Establish the connection to the database
engine = create_engine(DATABASE_URI)

metadata = MetaData()
metadata.reflect(bind=engine)

muscle_group_table = metadata.tables['muscle_group']

Session = sessionmaker(bind=engine)
session = Session()

df = pd.read_csv('../common_exercises.csv')

# Assuming 'equipment_details' is the column with the URLs and 'Equipment_x' is the column with the equipment names
unique_equipment = df[['muscle_gp_details', 'muscle_gp']].drop_duplicates()
unique_equipment = unique_equipment.dropna(subset=['muscle_gp'])
print(unique_equipment)

try:
    for index, muscle_group in unique_equipment.iterrows():
        # Check if the equipment already exists to avoid duplicates
        exists = session.query(muscle_group_table).filter_by(name=muscle_group['muscle_gp']).first() is not None
        if not exists:
            # Construct an insert statement for the equipment_table
            ins = muscle_group_table.insert().values(
                name=muscle_group['muscle_gp'], 
                muscle_group_details_url=muscle_group['muscle_gp_details']
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