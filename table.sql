create table tblMember(
 idx number primary KEY,
 name varchar2(10) not null,
 phone varchar2(13) not null,
 team varchar2(10) not null
);
create sequence seqmember;

