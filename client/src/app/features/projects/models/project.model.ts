// Interfaces / types will be the closest thing to actual Models in MVC. Angular is more MVVM.
export type ProjectStatus = 'ACTIVE' | 'ARCHIVED';

export interface ProjectResponse {
    projectId: string;
    projectName: string;
    projectDescription: string;
    projectStatus: ProjectStatus;
    createdByUserId: string;
    createdAt: string;
    updatedAt: string;
}
