from sqlalchemy import create_engine, MetaData, select, Table
from sqlalchemy.orm import sessionmaker
from sqlalchemy.exc import SQLAlchemyError
import pandas as pd

DATABASE_URI = 'postgresql://postgres:root@localhost:5432/authorization'

# Establish the connection to the database
engine = create_engine(DATABASE_URI)

metadata = MetaData()
metadata.reflect(bind=engine)

exercise_table = metadata.tables['exercise']
muscle_group_table = Table('muscle_group', metadata, autoload_with=engine)
equipment_table = metadata.tables['equipment']
difficulty_table = metadata.tables['difficulty']
category_table = metadata.tables['category']


Session = sessionmaker(bind=engine)
session = Session()

df = pd.read_csv('../common_exercises.csv')

df = df.drop_duplicates()

df = df.where(pd.notnull(df), None)

try:
    for index, exercise in df.iterrows():
        
        muscle_group_name = exercise['muscle_gp']
        query = select(muscle_group_table.c.id).where(muscle_group_table.c.name == muscle_group_name)
        result = session.execute(query).fetchone()  
        muscle_group_id = result[0] if result else None  
        
        difficulty_name = exercise['Level']
        query = select(difficulty_table.c.id).where(difficulty_table.c.dificulty_level == difficulty_name)
        result = session.execute(query).fetchone()  
        difficulty_id = result[0] if result else None 
        
        category_name = exercise['Type']
        query = select(category_table.c.id).where(category_table.c.name == category_name)
        result = session.execute(query).fetchone()  
        category_id = result[0] if result else None 
        
        equipment_name = exercise['Equipment_x']
        query = select(equipment_table.c.id).where(equipment_table.c.name == equipment_name)
        result = session.execute(query).fetchone()  
        equipment_id = result[0] if result else None 

        if muscle_group_id is None:
            print(f"No ID found for muscle group {muscle_group_name}")
            continue  # Skip this record or handle the error as you see fit
        
        ins = exercise_table.insert().values(
            name=exercise['Exercise_Name'], 
            description=exercise['Desc'],
            description_url=exercise['Description_URL'],
            exercise_image_start_url=exercise['Exercise_Image'],
            exercise_image_end_url=exercise['Exercise_Image1'],
            muscle_group_id=muscle_group_id,
            difficulty_id =difficulty_id,
            category_id = category_id,
            equipment_id = equipment_id,
            has_no_reps = exercise['hasNoReps'],
            has_weight = exercise['hasWeight'],
            rating = exercise['Rating_y'],
            is_exercise_exclusive = False,
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