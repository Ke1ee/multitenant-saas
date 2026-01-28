create table users (
  id uuid primary key,
  tenant_id uuid not null references tenants(id),
  email varchar(255) not null unique,
  password_hash varchar(255) not null,
  role varchar(50) not null,
  created_at timestamptz not null default now()
);

create index idx_users_tenant_id on users(tenant_id);
create index idx_users_email on users(email);