export type ProjectStatus = 'ACTIVE' | 'ARCHIVE';

export interface ProjectResponse {
    projectId: string;
    projectName: string;
    projectDescription: string;
    projectStatus: ProjectStatus;
    createdByUserId: string;
    createdAt: string;
    updatedAt: string;
}
export interface CreateProjectRequest {
  projectName: string;
  projectDescription?: string;
}

export interface UpdateProjectRequest {
  projectName?: string;
  projectDescription?: string;
  projectStatus?: ProjectStatus;
}
