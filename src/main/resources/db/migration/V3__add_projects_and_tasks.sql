create table projects (
  id uuid primary key,
  tenant_id uuid not null references tenants(id),
  name varchar(120) not null,
  description text,
  created_at timestamptz not null default now()
);

create table tasks (
  id uuid primary key,
  project_id uuid not null references projects(id),
  tenant_id uuid not null references tenants(id),
  title varchar(255) not null,
  description text,
  status varchar(50) not null default 'TODO',
  created_at timestamptz not null default now()
);

create index idx_projects_tenant_id on projects(tenant_id);
create index idx_tasks_project_id on tasks(project_id);
create index idx_tasks_tenant_id on tasks(tenant_id);