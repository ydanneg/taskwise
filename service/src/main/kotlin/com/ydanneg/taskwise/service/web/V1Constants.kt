package com.ydanneg.taskwise.service.web

object V1Constants {
    const val DEFAULT_PAGE_SIZE = 20

    const val TASKS = "/v1/tasks"
    const val USER_TASKS = "/v1/users/{userId}/tasks"

    fun userTasksUri(userId: String) = USER_TASKS.replace("{userId}", userId)
    fun taskByIdUri(taskId: String) = "$TASKS/$taskId"
    fun taskStatusUri(taskId: String) = "$TASKS/$taskId/status"
    fun taskPriorityUri(taskId: String) = "$TASKS/$taskId/priority"
    fun taskDueDateUri(taskId: String) = "$TASKS/$taskId/due-date"
    fun taskTitleUri(taskId: String) = "$TASKS/$taskId/title"
    fun taskDescriptionUri(taskId: String) = "$TASKS/$taskId/description"
    fun taskAssigneeUri(taskId: String) = "$TASKS/$taskId/assignee"
}