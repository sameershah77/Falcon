package com.example.falcon.model

class TaskModel {
    var projectDetails: ProjectDetails = ProjectDetails()
    var myTask: Task = Task()
    constructor() {}

    constructor(projectDetails: ProjectDetails, myTask:Task) {
        this.projectDetails = projectDetails
        this.myTask = myTask
    }
}