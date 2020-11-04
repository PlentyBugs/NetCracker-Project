delete from team_profession;
delete from usr_team;
delete from team;

insert into team(id, logo_filename, team_name) VALUES
(1, 'teamLogo.png', 'Team A'),
(2, 'teamLogo.png', 'Train B'),
(3, 'teamLogo.png', 'FunCo');
insert into usr_team(usr_id, team_id) values
(1, 1),
(1, 2),
(3, 3),
(4, 3),
(4, 1);
insert into team_profession(team_id, professions) VALUES
(1, 'GAME_DESIGNER'), (1, 'MUSICIAN'), (1, 'TESTER'), (1, 'PROGRAMMER'), (1, 'BACKEND'),
(2, 'PROGRAMMER'),
(3, 'GAME_DESIGNER'), (3, 'MUSICIAN'), (3, 'TESTER'), (3, 'PROGRAMMER'), (3, 'BACKEND'), (3, 'DESIGNER'), (3, 'BIOTECHNOLOGIST');