-- Add starter book records after the application has initialized the schema.
INSERT INTO books (title, author, isbn, genre, publisher, publication_year, quantity, available_copies, description, created_at, updated_at) VALUES
('Clean Code','Robert C. Martin','9780132350884','Software Engineering','Prentice Hall',2008,4,4,'A handbook of agile software craftsmanship.',NOW(),NOW()),
('Design Patterns','Erich Gamma et al.','9780201633610','Software Engineering','Addison-Wesley',1994,3,3,'Elements of reusable object-oriented software.',NOW(),NOW()),
('The Pragmatic Programmer','Andrew Hunt','9780135957059','Software Engineering','Addison-Wesley',2019,5,5,'Your journey to mastery.',NOW(),NOW()),
('Introduction to Algorithms','Thomas H. Cormen','9780262046305','Computer Science','MIT Press',2022,2,2,'A comprehensive introduction to algorithms.',NOW(),NOW()),
('The Linux Programming Interface','Michael Kerrisk','9781593272203','Operating Systems','No Starch Press',2010,2,2,'A Linux and UNIX system programming handbook.',NOW(),NOW());
