delete from competition;

insert into competition (id, comp_name, description, end_date, start_date, title_filename, usr_id, comp_ended, group_chat_id) values
(1, 'Hackathon', 'Hackathon', '2020-03-12 12:45:00.000000', '2020-01-12 12:45:00.000000', 'compTitle.png', 4, false, 'e152e6eb-d67c-4e6f-9304-e752b6d11575'),
(2, 'Hackathon 2', 'Hackathon 2', '2022-09-12 12:45:00.000000', '2022-01-12 12:45:00.000000', 'compTitle.png', 3, false, 'e152e6eb-d67c-4e6f-9304-e752b6d11549'),
(3, 'Hackathon 3', 'Hackathon 3', '2020-08-12 12:45:00.000000', '2020-01-12 12:45:00.000000', 'compTitle.png', 3, false, 'e152e6eb-d67c-4e6f-1804-e752b6d11549'),
(4, 'Big Data Analysis MegaHackathon Moscow 2021', 'Participate and be a part of history, let megacorporations spot you and reserve a spot for you', '2021-09-12 18:15:00.000000', '2021-01-12 17:55:00.000000', 'compTitle.png', 3, false, 'e179e6eb-d67c-4e6f-9304-e752b6d11549'),
(5,'Hackathon 5','that is description','2020-06-07 12:45:00.000000','2020-06-06 13:00:00.000000','compTitle.png',2,false,'e152e6eb-d67c-4e6f-9304-e752b6d11575');

alter sequence hibernate_sequence restart with 10;