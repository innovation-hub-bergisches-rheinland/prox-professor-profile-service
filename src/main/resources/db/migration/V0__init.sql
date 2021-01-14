create table faculty
(
	id uuid not null
		constraint faculty_pkey
			primary key,
	abbreviation varchar(3),
	name varchar(255)
);

create table professor
(
	id uuid not null
		constraint professor_pkey
			primary key,
	affiliation varchar(255),
	consultation_hour varchar(255),
	email varchar(255),
	homepage varchar(255),
	room varchar(255),
	telephone varchar(255),
	creator_id uuid
		constraint uk_avnjfd28y234csqk5rlylck95
			unique,
	main_subject varchar(255),
	name varchar(255),
	data oid,
	vita text,
	faculty_id uuid
		constraint fkd1eougli9k1sdoq78cme3rnjg
			references faculty
);

create table professor_publications
(
	professor_id uuid not null
		constraint fkr0yvri66oyp93tlo581hqh6nh
			references professor,
	publication varchar(255)
);

create table professor_research_subjects
(
	professor_id uuid not null
		constraint fkmp1mqegvplkwj44hv96duttg1
			references professor,
	subject varchar(255)
);
