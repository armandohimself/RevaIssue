export type ProjectRole = 'TESTER' | 'DEVELOPER' | 'ADMIN';

export interface ProjectAccessResponse {
  projectAccessId: string;
  projectId: string;
  projectRole: ProjectRole;
  userId: string;
}

export interface GrantProjectAccessRequest {
  userId: string;
  projectRole: ProjectRole;
}
