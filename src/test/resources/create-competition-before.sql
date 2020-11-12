delete from competition;

insert into competition (id, comp_name, description, end_date, start_date, title_filename, usr_id, comp_ended) values
(1, 'Hackathon', 'Hackathon', '2020-08-12 12:45:00.000000', '2020-01-12 12:45:00.000000', 'compTitle.png', 4, false),
(2, 'Hackathon 2', 'Hackathon 2', '2022-09-12 12:45:00.000000', '2022-01-12 12:45:00.000000', 'compTitle.png', 3, false),
(3, 'Hackathon 3', 'Hackathon 3', '2024-08-12 12:45:00.000000', '2024-01-12 12:45:00.000000', 'compTitle.png', 3, false),
(4, 'Big Data Analysis MegaHackathon Moscow 2021', 'Participate and be a part of history, let megacorporations spot you and reserve a spot for you', '2021-09-12 18:15:00.000000', '2021-01-12 17:55:00.000000', 'compTitle.png', 3, false);

alter sequence hibernate_sequence restart with 10;