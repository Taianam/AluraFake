package br.com.alura.AluraFake.course.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.alura.AluraFake.course.dto.InstructorCoursesReportDTO;
import br.com.alura.AluraFake.course.entity.Course;
import br.com.alura.AluraFake.course.entity.Status;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.task.entity.Task;
import br.com.alura.AluraFake.task.entity.Type;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.user.entity.Role;
import br.com.alura.AluraFake.user.entity.User;
import br.com.alura.AluraFake.user.repository.UserRepository;

@Service
public class CourseService {

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private UserRepository userRepository;

	public void publishCourse(Long courseId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));

		if (course.getStatus() != Status.BUILDING) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Curso só pode ser publicado se o status for BUILDING");
		}

		List<Task> tasks = taskRepository.findByCourseIdOrderByOrderNumber(courseId);

		if (tasks.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Curso deve ter pelo menos uma atividade de cada tipo");
		}

		Set<Type> taskTypes = tasks.stream().map(Task::getType).collect(Collectors.toSet());
		if (!taskTypes.containsAll(Set.of(Type.OPEN_TEXT, Type.SINGLE_CHOICE, Type.MULTIPLE_CHOICE))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Curso deve ter pelo menos uma atividade de cada tipo");
		}

		for (int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).getOrderNumber() != i + 1) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Atividades devem ter sequência contínua de ordem");
			}
		}

		course.setStatus(Status.PUBLISHED);
		course.setPublishedAt(LocalDateTime.now());
		courseRepository.save(course);
	}

	public InstructorCoursesReportDTO getInstructorCoursesReport(Long instructorId) {
		User instructor = userRepository.findById(instructorId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

		if (instructor.getRole() != Role.INSTRUCTOR) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não é um instrutor");
		}

		List<Course> courses = courseRepository.findByInstructorId(instructorId);

		List<InstructorCoursesReportDTO.CourseReportItem> courseItems = courses.stream().map(course -> {
			int taskCount = taskRepository.countByCourseId(course.getId()).intValue();
			return new InstructorCoursesReportDTO.CourseReportItem(course.getId(), course.getTitle(),
					course.getStatus(), course.getPublishedAt(), taskCount);
		}).collect(Collectors.toList());

		long publishedCoursesCount = courses.stream().filter(course -> course.getStatus() == Status.PUBLISHED).count();

		return new InstructorCoursesReportDTO(courseItems, publishedCoursesCount);
	}
}