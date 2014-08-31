--liquibase formatted sql

alter table article add community varchar(20) null;
create index idx_community_article on article (community);
alter table article add constraint fk_community_article foreign key (community) references community(id) on delete set null;

alter table attendee add community varchar(20) null;
create index idx_community_attendee on attendee (community);
alter table attendee add constraint fk_community_attendee foreign key (community) references community(id) on delete set null;

alter table attendee_session add community varchar(20) null;
create index idx_community_attendee_session on attendee_session (community);
alter table attendee_session add constraint fk_community_attendee_session foreign key (community) references community(id) on delete set null;

alter table event add community varchar(20) null;
create index idx_community_event on event (community);
alter table event add constraint fk_community_event foreign key (community) references community(id) on delete set null;

alter table event_venue add community varchar(20) null;
create index idx_community_event_venue on event_venue (community);
alter table event_venue add constraint fk_community_event_venue foreign key (community) references community(id) on delete set null;

alter table historical_message add community varchar(20) null;
create index idx_community_historical_message on historical_message (community);
alter table historical_message add constraint fk_community_historical_message foreign key (community) references community(id) on delete set null;

alter table mailing_list add community varchar(20) null;
create index idx_community_mailing_list on mailing_list (community);
alter table mailing_list add constraint fk_community_mailing_list foreign key (community) references community(id) on delete set null;

alter table mailing_list_message add community varchar(20) null;
create index idx_community_mailing_list_message on mailing_list_message (community);
alter table mailing_list_message add constraint fk_community_mailing_list_message foreign key (community) references community(id) on delete set null;

alter table message_template add community varchar(20) null;
create index idx_community_message_template on message_template (community);
alter table message_template add constraint fk_community_message_template foreign key (community) references community(id) on delete set null;

alter table partner add community varchar(20) null;
create index idx_community_partner on partner (community);
alter table partner add constraint fk_community_partner foreign key (community) references community(id) on delete set null;

alter table representative add community varchar(20) null;
create index idx_community_representative on representative (community);
alter table representative add constraint fk_community_representative foreign key (community) references community(id) on delete set null;

alter table room add community varchar(20) null;
create index idx_community_room on room (community);
alter table room add constraint fk_community_room foreign key (community) references community(id) on delete set null;

alter table session add community varchar(20) null;
create index idx_community_session on session (community);
alter table session add constraint fk_community_session foreign key (community) references community(id) on delete set null;

alter table slot add community varchar(20) null;
create index idx_community_slot on slot (community);
alter table slot add constraint fk_community_slot foreign key (community) references community(id) on delete set null;

alter table speaker add community varchar(20) null;
create index idx_community_speaker on speaker (community);
alter table speaker add constraint fk_community_speaker foreign key (community) references community(id) on delete set null;

alter table speaker_session add community varchar(20) null;
create index idx_community_speaker_session on speaker_session (community);
alter table speaker_session add constraint fk_community_speaker_session foreign key (community) references community(id) on delete set null;

alter table sponsorship_event add community varchar(20) null;
create index idx_community_sponsorship_event on sponsorship_event (community);
alter table sponsorship_event add constraint fk_community_sponsorship_event foreign key (community) references community(id) on delete set null;

alter table topic add community varchar(20) null;
create index idx_community_topic on topic (community);
alter table topic add constraint fk_community_topic foreign key (community) references community(id) on delete set null;

alter table track add community varchar(20) null;
create index idx_community_track on track (community);
alter table track add constraint fk_community_track foreign key (community) references community(id) on delete set null;

alter table venue add community varchar(20) null;
create index idx_community_venue on venue (community);
alter table venue add constraint fk_community_venue foreign key (community) references community(id) on delete set null;

alter table web_source add community varchar(20) null;
create index idx_community_web_source on web_source (community);
alter table web_source add constraint fk_community_web_source foreign key (community) references community(id) on delete set null;