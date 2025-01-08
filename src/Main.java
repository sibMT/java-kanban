import Classes.Epic;
import Classes.Subtask;
import Classes.Task;
import Classes.TaskStatus;
import Controllers.InMemoryHistoryManager;
import Controllers.InMemoryTaskManager;
import Controllers.Managers;
import Controllers.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

//        Task task = new Task("уборка квартиры","мойка", TaskStatus.DONE);
//        Task task1 = new Task("учеба java","спринт 4",TaskStatus.DONE);
//        Epic epic = new Epic("Разобраться с теорией","Наследование");
//        Epic epic1 = new Epic("Задача","Функции");
//        Subtask subtask = new Subtask(epic,"Классы","особенности", TaskStatus.DONE);
//        Subtask subtask1Epic1 = new Subtask(epic1,"Линейные уравнения","Выход за пределы",TaskStatus.DONE);
//        Subtask subtask2Epic1 = new Subtask(epic1,"Основы 11 класс","примеры из 2015", TaskStatus.DONE);
//        taskManager.createTask(task);
//        taskManager.createTask(task1);
//        taskManager.createEpic(epic);
//        taskManager.createEpic(epic1);
//        taskManager.createSubtasks(subtask);
//        taskManager.createSubtasks(subtask1Epic1);
//        taskManager.createSubtasks(subtask2Epic1);
//        System.out.println(taskManager.getAllTasks());
//        System.out.println(taskManager.getAllEpics());
//        System.out.println(taskManager.getAllSubtasks());
//        task.setTaskStatus(TaskStatus.IN_PROGRESS);
//        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
//        epic1.setTaskStatus(TaskStatus.IN_PROGRESS);
//        taskManager.removeTaskById(1);
//        taskManager.removeEpicById(3);
//        System.out.println(taskManager.getAllEpics());



    }
}
