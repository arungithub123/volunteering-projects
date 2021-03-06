package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import model.Project.StatusProject;
import model.Task.StatusTask;

public class Administrator {
	public SessionFactory sessionFactory;

	public Administrator(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void createProject() throws ParseException {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		SimpleDateFormat formatterDate = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm");
		File file = new File("F:\test.jpg");
		byte[] imageData = new byte[(int) file.length()];

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(imageData);
			fileInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// List of benefits
		List<String> benefits = new ArrayList();
		benefits.add("Incentives");
		benefits.add("Overtime");
		benefits.add("Bonus");
		List<String> resources = new ArrayList<>();
		resources.add("Java Developers");
		resources.add("15 Computers");
		// Task1
		Task task = new Task("Requirement Gathering", formatterTime.parse("08:56"), resources, StatusTask.INACTIVE,
				imageData);
		// Task
		Task task2 = new Task("Designing", formatterTime.parse("11:53"), resources, StatusTask.INACTIVE, imageData);
		Project project = new Project("Inventory Managment System", formatterDate.parse("03/24/2010"),
				formatterDate.parse("03/24/2014"), StatusProject.ACTIVE, benefits);
		project.getTaskList().add(task);
		project.getTaskList().add(task2);
		session.save(project);
		tx.commit();
		session.close();

	}

	// Updating project status
	public void updateProjectStatus() throws IOException {
		Session session = sessionFactory.openSession();
		session.getTransaction().begin();
		System.out.println("Enter Project ID: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String n = br.readLine();
		System.out.println(
				"Enter project status(PENDING, ACTIVE, INACTIVE, DELETED, COMPLELETED)-Type any one of these ");
		String status = br.readLine();
		Query query = session.createSQLQuery("update project set statusProject = :status" + " where ProjectID = :id");
		query.setParameter("status", status.trim());
		query.setParameter("id", n);
		int result = query.executeUpdate();
		session.getTransaction().commit();
		if (result >= 1)
			System.out.println("Updated Successfully");
		else
			System.out.println("ID does not exist");
		session.close();

	}

	// Updating project Task
	public void updateProjectTaskStatus() throws IOException {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String pidv = br.readLine();
		Query query = session.createQuery("Select project from Project project where ProjectID=:pid");
		query.setParameter("pid", pidv.trim());
		List<Project> projectList = query.list();
		System.out.println("Size=" + projectList.size());

		if (projectList.size() == 1) {
			System.out.println("Enter Task ID: ");
			String tid = br.readLine();
			System.out.println(
					"Enter project status(PENDING(0), ACTIVE(1), INACTIVE(2), COMPLELETED(3))-Type any one of these ");
			String status = br.readLine();
			for (Project p : projectList) {
				for (Task t : p.getTaskList()) {
					
					if (t.getTaskID() == Integer.parseInt(tid)) {
						System.out.println("How are you?");
						if (status.equals("0"))
							t.setStatusTask(StatusTask.PENDING);
						if (status.equals("1"))
							t.setStatusTask(StatusTask.ACTIVE);
						if (status.equals("2"))
							t.setStatusTask(StatusTask.INACTIVE);
						if (status.equals("3"))
							t.setStatusTask(StatusTask.COMPLELETED);
					}
				}
				session.persist(p);
			}
		}
		tx.commit();
		session.close();
	}
}
