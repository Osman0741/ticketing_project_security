package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Task;
import com.cydeo.enums.Status;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.TaskMapper;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectMapper projectMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, ProjectMapper projectMapper, UserService userService, UserMapper userMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectMapper = projectMapper;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public List<TaskDTO> listAllTasks() {

        return taskRepository.findAll().stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void save(TaskDTO dto) {
        dto.setTaskStatus(Status.OPEN);
        dto.setAssignedDate(LocalDate.now());
        taskRepository.save(taskMapper.convertToEntity(dto));

    }

    @Override
    public void update(TaskDTO dto) {

        Optional<Task> task = taskRepository.findById(dto.getId());
        Task converted = taskMapper.convertToEntity(dto);

        if(task.isPresent()){
            converted.setTaskStatus(dto.getTaskStatus()== null ? task.get().getTaskStatus() : dto.getTaskStatus());
            converted.setAssignedDate(task.get().getAssignedDate());
            taskRepository.save(converted);
        }

    }

    @Override
    public void delete(Long id) {

     Optional<Task>  task =  taskRepository.findById(id);
     if(task.isPresent()){
         task.get().setIsDeleted(true);
         taskRepository.save(task.get());
     }

    }

    @Override
    public TaskDTO findById(Long id) {

        Optional<Task> task = taskRepository.findById(id);

        if(task.isPresent()){

            return taskMapper.convertToDto(task.get());

        }
        return null;
    }

    @Override
    public int nonCompletedTask(String projectCode) {
        return taskRepository.nonCompletedTask(projectCode);
    }

    @Override
    public int completedTask(String projectCode) {
        return taskRepository.completedTask(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO projectDTO) {

        List<Task> list = taskRepository.findAllByProject(projectMapper.convertToEntity(projectDTO));

        list.forEach(task-> delete(task.getId()));


    }

    @Override
    public void completeByProject(ProjectDTO projectDTO) {

        List<Task> list = taskRepository.findAllByProject(projectMapper.convertToEntity(projectDTO));

        list.stream().map(taskMapper::convertToDto).forEach(taskDto-> {

            taskDto.setTaskStatus(Status.COMPLETE);
            update(taskDto);


        });

    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) {

        UserDTO loginUser = userService.findByUserName("john@employee.com");

        List<Task> list = taskRepository.findAllByTaskStatusIsNotAndAssignedEmployee(status, userMapper.convertToEntity(loginUser));

        return list.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByStatus(Status status) {

        UserDTO loginUser = userService.findByUserName("john@employee.com");

        List<Task> list = taskRepository.findAllByTaskStatusAndAssignedEmployee(status, userMapper.convertToEntity(loginUser));

        return list.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllNonCompletedByAssignedEmployee(UserDTO assignedEmployee) {
        List<Task> tasks = taskRepository
                .findAllByTaskStatusIsNotAndAssignedEmployee(Status.COMPLETE, userMapper.convertToEntity(assignedEmployee));
        return tasks.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }
}
