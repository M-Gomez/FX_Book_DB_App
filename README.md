# FX_Book_DB_App
A JavaFX based application to track a library of books, their authors, author royalties, publishers, and information associated with all of those entities.
The data for these objects is stored in a MySQL database (A copy of which is @ /config/FX_Book_DB.sql) that the app interfaces with through a JDBC plugin.
Logs are taken using Log4J, and JSON is read through json-simple
config.json contains fields for DB login credentials and address.
It may be necessary to create a folder named "logs" in the root directory in order for the logger to work correctly.
[A short demo of the application can be seen here](https://www.youtube.com/watch?v=Fpx2UzcBc2I&feature=youtu.be "Click Me!")
