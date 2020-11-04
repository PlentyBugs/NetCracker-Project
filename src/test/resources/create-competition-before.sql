delete from competition;
delete from statistics;

insert into competition (id, comp_name, description, end_date, start_date, title_filename, usr_id) values
(1, 'Hackathon', 'Hackathon', '2020-08-12 12:45:00.000000', '2020-01-12 12:45:00.000000', 'compTitle.png', 3),
(2, 'Big Data Analysis MegaHackathon Moscow 2021', 'Participate and be a part of history, let megacorporations spot you and reserve a spot for you', '2021-09-12 18:15:00.000000', '2021-01-12 17:55:00.000000', 'compTitle.png', 4);
insert into statistics(team_id, comp_id) VALUES
(1, 1),
(1, 2),
(2, 2);