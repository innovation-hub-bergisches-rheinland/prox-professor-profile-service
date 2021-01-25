create table faculty (id uuid not null, abbreviation varchar(255), name varchar(255), primary key (id));
create table professor (id uuid not null, affiliation varchar(255), collegePage varchar(255), consultation_hour varchar(255), email varchar(255), homepage varchar(255), room varchar(255), telephone varchar(255), creator_id uuid, main_subject varchar(255), name varchar(255), filename varchar(255), vita text, faculty_id uuid, primary key (id));
create table professor_publications (professor_id uuid not null, publication varchar(255));
create table professor_research_subjects (professor_id uuid not null, subject varchar(255));
alter table faculty add constraint UKkvktiyqe10e4ts4m9w7demr8t unique (abbreviation, name);
alter table professor add constraint UK_avnjfd28y234csqk5rlylck95 unique (creator_id);
alter table professor add constraint FKd1eougli9k1sdoq78cme3rnjg foreign key (faculty_id) references faculty;
alter table professor_publications add constraint FKr0yvri66oyp93tlo581hqh6nh foreign key (professor_id) references professor;
alter table professor_research_subjects add constraint FKmp1mqegvplkwj44hv96duttg1 foreign key (professor_id) references professor;
