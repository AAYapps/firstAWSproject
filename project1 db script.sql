create table ERS_USER_ROLES(
    ERS_USER_ROLE_ID number primary key,
    USER_ROLE varchar2(10)
);

insert into Ers_User_Roles(ERS_USER_ROLE_ID, USER_ROLE) values (1, 'Employee');

insert into Ers_User_Roles(ERS_USER_ROLE_ID, USER_ROLE) values (2, 'Finance');

create table ERS_REIMBURSEMENT_TYPE(
    REIMB_TYPE_ID number primary key,
    REIMB_TYPE varchar2(10)
);

-- LODGING, TRAVEL, FOOD, or OTHER

insert into ERS_REIMBURSEMENT_TYPE(REIMB_TYPE_ID, REIMB_TYPE) values (1, 'LODGING');

insert into ERS_REIMBURSEMENT_TYPE(REIMB_TYPE_ID, REIMB_TYPE) values (2, 'TRAVEL');

insert into ERS_REIMBURSEMENT_TYPE(REIMB_TYPE_ID, REIMB_TYPE) values (3, 'FOOD');

insert into ERS_REIMBURSEMENT_TYPE(REIMB_TYPE_ID, REIMB_TYPE) values (4, 'OTHER');

create table ERS_REIMBURSEMENT_STATUS(
    REIMB_STATUS_ID number primary key,
    REIMB_STATUS varchar2(10)
);

insert into ERS_REIMBURSEMENT_STATUS(REIMB_STATUS_ID, REIMB_STATUS) values (1, 'Pending');

insert into ERS_REIMBURSEMENT_STATUS(REIMB_STATUS_ID, REIMB_STATUS) values (2, 'Approved');

insert into ERS_REIMBURSEMENT_STATUS(REIMB_STATUS_ID, REIMB_STATUS) values (3, 'Denied');

create table ERS_USERS(
    ERS_USERS_ID number primary key,
    ERS_USERNAME varchar2(50) unique not null,
    ERS_PASSWORD varchar2(50) not null,
    USER_FIRST_NAME varchar2(100) not null,
    USER_LAST_NAME varchar2(100) not null,
    USER_EMAIL varchar2(100) unique not null,
    USER_ROLE_ID number not null,
    foreign key (USER_ROLE_ID) REFERENCES ERS_USER_ROLES(ERS_USER_ROLE_ID)
);

create sequence add_Ers_user_id
    start with 1
    increment by 1;

create or replace trigger Ers_create_user
before Insert on ERS_USERS
for each row
begin
    select add_Ers_user_id.nextval INTO :new.ERS_USERS_ID FROM DUAL;
end;
/

insert into ERS_USERS(ERS_USERNAME, ERS_PASSWORD, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_ROLE_ID) values('kswan', '-2041944739', 'Kathryn', 'Swan', 'kswan@gmail.com', 2);

insert into ERS_USERS(ERS_USERNAME, ERS_PASSWORD, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_ROLE_ID) values('vcorry', '613930047', 'Valarie', 'Corry', 'vcorry@gmail.com', 2);

insert into ERS_USERS(ERS_USERNAME, ERS_PASSWORD, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_ROLE_ID) values('mstuart', '-435398315', 'Maryrose', 'Stuart', 'mstuart@gmail.com', 2);

select u.ERS_USERNAME, u.user_first_name, u.user_last_name, u.user_email, r.user_role from ers_users u 
    inner join ERS_USER_ROLES r on u.user_role_id = r.ers_user_role_id
    where u.ers_users_id = 21;

create or replace procedure ErsCreateAccount(
    u_username in varchar2, u_password in varchar2, u_firstname in varchar2, u_lastname in varchar2, Email in VARCHAR2, return_status out number
)
as
begin
    insert into ERS_USERS(ERS_USERNAME, ERS_PASSWORD, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_ROLE_ID)
        values(u_username, u_password, u_firstname, u_lastname, Email, 1);
    return_status := 1;
EXCEPTION
    when OTHERS THEN
        return_status := 0;
end;
/

create or replace procedure Ers_login(
    temp_username in varchar2, temp_password in varchar2, out_result out number, email_alert out varchar2
)
as
    entries_avalable number; u_usernameid number; pass number;
begin
    select count(*) into entries_avalable from ERS_USERS u where u.ers_username = temp_username;
    if entries_avalable != 0 then
        select u.ers_users_id into u_usernameid from ERS_USERS u where u.ers_username = temp_username;
        select count(*) into pass from ERS_USERS u where u.ers_users_id = u_usernameid and u.ers_password = temp_password;
        if pass = 1 then
            out_result := u_usernameid;
        else
            out_result := 0;
            select u.user_email into email_alert from ERS_USERS u where u.ers_username = temp_username;
        end if;
    else
        out_result := -1;
    end if;
end;
/

commit;

create table ERS_REIMBURSEMENT(
    REIMB_ID number PRIMARY key,
    REIMB_amount number not null,
    REIMB_submitted TIMESTAMP not null,
    REIMB_resolved TIMESTAMP,
    REIMB_Description varchar2(250),
    REIMB_receipt blob,
    REIMB_author number not null,
    REIMB_resolver number,
    REIMB_status_ID number not null,
    REIMB_type_ID number not null,
    foreign key (REIMB_author) REFERENCES ERS_USERS(ERS_USERS_ID),
    foreign key (REIMB_resolver) REFERENCES ERS_USERS(ERS_USERS_ID),
    foreign key (REIMB_status_ID) REFERENCES ERS_REIMBURSEMENT_STATUS(REIMB_STATUS_ID),
    foreign key (REIMB_type_ID) REFERENCES ERS_REIMBURSEMENT_TYPE(REIMB_TYPE_ID)
);

insert into ERS_REIMBURSEMENT(REIMB_amount, REIMB_submitted, REIMB_resolved, REIMB_Description, REIMB_author, REIMB_resolver, REIMB_status_ID, REIMB_type_ID)
    values (3, '13-Feb-19', '14-Feb-19', 'Used on the Market Frankard Line', 21, 1, 2, 2);

insert into ERS_REIMBURSEMENT(REIMB_amount, REIMB_submitted, REIMB_resolved, REIMB_Description, REIMB_author, REIMB_resolver, REIMB_status_ID, REIMB_type_ID)
    values (49, '9-Feb-19', '11-Feb-19', 'Out drinking all night', 21, 2, 3, 4);



create sequence add_Ers_REIMB_ID
    start with 1
    increment by 1;

create or replace trigger Ers_create_REIMB
before Insert on ERS_REIMBURSEMENT
for each row
begin
    select add_Ers_REIMB_ID.nextval INTO :new.REIMB_ID FROM DUAL;
end;
/

create or replace procedure ERS_Create_REIMBURSEMENT(
    u_amount in number, u_Description in varchar2, u_receipt in blob, author in varchar2, u_type in varchar2, return_status out number
)
as
    u_id number; u_type_ID number;
begin
    select u.ERS_USERS_ID into u_id from ERS_USERS u where u.ers_username = author;
    select rt.reimb_type_id into u_type_ID from ERS_REIMBURSEMENT_TYPE rt where rt.reimb_type = u_type;
    insert into ERS_REIMBURSEMENT(REIMB_amount, REIMB_submitted, REIMB_Description, REIMB_receipt, REIMB_author, REIMB_status_ID, REIMB_type_ID) 
    values (u_amount, current_timestamp, u_Description, u_receipt, u_id, 1, u_type_ID);
    return_status := 1;
EXCEPTION
    when OTHERS THEN
        return_status := 0;
end;
/

create or replace procedure Ers_Resolve_REIMBURSEMENT(
    u_id in number, r_username in varchar2, r_status in number, r_result out number
)
as
    r_id number;
begin
    select ERS_USERS_ID into r_id from ERS_USERS u where u.ERS_USERNAME = r_username;
    update ERS_REIMBURSEMENT 
    set REIMB_status_ID = r_status, REIMB_resolver = r_id, REIMB_resolved = current_timestamp
    where REIMB_ID = u_id;
    r_result := 1;
EXCEPTION
    when OTHERS THEN
        r_result := 0;
end;
/

declare
    results number;
begin
    Ers_Resolve_REIMBURSEMENT(61, 'kswan', 3, results);
    DBMS_OUTPUT.PUT_LINE('result: ' || results);
end;
/

select * from ERS_REIMBURSEMENT;

select * from ers_users u where u.user_email = 'kswadn';
rollback;

select r.reimb_amount as amount, r.reimb_submitted as submitted, r.reimb_resolved as resolved, r.reimb_description as description, 
    u.USER_FIRST_NAME as authorfirst, u.USER_LAST_NAME as authorlast, u.USER_Email as authoremail
        from ers_users u
        inner join ers_reimbursement r on u.ers_users_id = r.REIMB_author
        where u.ers_username = 'mthompson';

select r.reimb_amount as amount, r.reimb_submitted as submitted, r.reimb_resolved as resolved, r.reimb_description as description, 
    u.USER_FIRST_NAME as authorfirst, u.USER_LAST_NAME as authorlast, u.USER_Email as authoremail, r.reimb_resolver as resolver,
    rs.reimb_status as status, rt.reimb_type as type
        from ers_users u
        inner join ers_reimbursement r on u.ers_users_id = r.REIMB_author
        inner join ERS_REIMBURSEMENT_STATUS rs on rs.reimb_status_id = r.reimb_status_id
        inner join ERS_REIMBURSEMENT_TYPE rt on rt.reimb_type_id = r.reimb_type_id
        where u.ers_username = 'mthompson';

select r.reimb_amount as amount, r.reimb_submitted as submitted, r.reimb_resolved as resolved, r.reimb_description as description, 
    u.USER_FIRST_NAME as authorfirst, u.USER_LAST_NAME as authorlast, u.USER_Email as authoremail, 
    a.USER_FIRST_NAME as resolverfirst, a.USER_LAST_NAME as resolverlast, a.USER_Email as resolveremail, 
    rs.reimb_status as status, rt.reimb_type as type
        from ers_users u
        inner join ers_reimbursement r on u.ers_users_id = r.REIMB_author
        inner join ERS_REIMBURSEMENT_STATUS rs on rs.reimb_status_id = r.reimb_status_id
        inner join ERS_REIMBURSEMENT_TYPE rt on rt.reimb_type_id = r.reimb_type_id
        left join ers_users a on a.ers_users_id = r.reimb_resolver
        where u.ers_username = 'mthompson';
