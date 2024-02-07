package com.ydanneg.taskwise.service.web

object V1Constants {
    const val TASKS = "/v1/tasks"
    const val USER_TASKS = "/v1/users/{userId}/tasks"

    fun userTasksUri(userId: String) = USER_TASKS.replace("{userId}", userId)
    fun taskByIdUri(taskId: String) = "$TASKS/$taskId"
}