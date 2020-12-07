create sequence hibernate_sequence start 1 increment 1;

create table competition (id int8 not null,
	comp_ended boolean not null,
	comp_name varchar(255),
	description varchar(2048),
	end_date timestamp,
	group_chat_id varchar(255),
	prize_fund int8 check (prize_fund>=0),
	start_date timestamp,
	title_filename varchar(255) not null,
	usr_id int8,
	primary key (id)
);

create table competition_theme (comp_id int8 not null,
	themes varchar(255)
);

create table registered_team (id int8 not null,
	group_chat_id varchar(255),
	team_name varchar(255),
	primary key (id)
);

create table result_type (usr_id int8 not null,
	result varchar(255),
	team_id int8 not null);

create table team (id int8 not null,
	group_chat_id varchar(255),
	logo_filename varchar(255) not null,
	team_name varchar(255),
	usr_id int8,
	primary key (id)
);

create table team_competition (team_id int8 not null,
	comp_id int8 not null,
	primary key (comp_id,
	team_id)
);

create table team_statistics (statfk int8 not null,
	competition_id int8,
	result int4);

create table team_user_team_role (userteamrolefk int8 not null,
	team_role int4,
	user_id int8);

create table user_statistics (statfk int8 not null,
	competition_id int8,
	result int4);

create table usr (id int8 not null,
	activation_code varchar(255),
	active boolean not null,
	avatar_filename varchar(255) not null,
	email varchar(255),
	name varchar(255),
	password varchar(255) not null,
	sec_name varchar(255),
	surname varchar(255),
	username varchar(255),
	primary key (id)
);

create table usr_registered_team (team_id int8 not null,
	usr_id int8 not null,
	primary key (team_id,
	usr_id)
);

create table usr_role (usr_id int8 not null,
	roles varchar(255)
);

create table usr_team (usr_id int8 not null,
	team_id int8 not null,
	primary key (team_id,
	usr_id)
);

create table usr_team_role (usr_id int8 not null,
	team_roles varchar(255)
);

alter table if exists registered_team add constraint UK_rpdodxqu8nf5dvvyye40q25vx unique (team_name);
alter table if exists team add constraint UK_sob22siqdnn2rfsxk6f00pgwb unique (team_name);
alter table if exists competition add constraint FKr4ookofn37ppf0d5ua8vrrtyn foreign key (usr_id) references usr;
alter table if exists competition_theme add constraint FKcybt3knfh83gdllshwep933w9 foreign key (comp_id) references competition;
alter table if exists result_type add constraint FKalsxfh3hxcx65mey1g9oenl6g foreign key (usr_id) references usr;
alter table if exists result_type add constraint FK34ywj7d3pxcjyu92rebr8u2h0 foreign key (team_id) references team;
alter table if exists team add constraint FKlp7t9hw8ocr68fwsr4b5otlgr foreign key (usr_id) references usr;
alter table if exists team_competition add constraint FK4lobjov0me3y9ru8qecewxrtt foreign key (comp_id) references competition;
alter table if exists team_competition add constraint FKk9fuvx4optv4p5tlmnw3lof5a foreign key (team_id) references team;
alter table if exists team_statistics add constraint FKi5907pxi7gsnr076vbkpub0sb foreign key (statfk) references team;
alter table if exists team_user_team_role add constraint FK19s6br0hxcaxyjucgul2abfcx foreign key (userteamrolefk) references team;
alter table if exists user_statistics add constraint FKfb00ik4alucpqy8wm5ukj7fin foreign key (statfk) references usr;
alter table if exists usr_registered_team add constraint FK3g6wplaajcjwg50b6cu13o577 foreign key (usr_id) references usr;
alter table if exists usr_registered_team add constraint FK6f0wquxw6c0h16j16l3fr6drd foreign key (team_id) references registered_team;
alter table if exists usr_role add constraint FK9ffk6ts9njcytrt8ft17fvr3p foreign key (usr_id) references usr;
alter table if exists usr_team add constraint FKrduohtnfqsr9sk4m0dq5ywdwh foreign key (team_id) references team;
alter table if exists usr_team add constraint FKgl4145bhwjx9040til8joyy2x foreign key (usr_id) references usr;
alter table if exists usr_team_role add constraint FKqtf6x7t8xfvlochthjjcyxmkp foreign key (usr_id) references usr;