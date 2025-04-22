import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;

// Abstract class for all users
abstract class user {
    private String name;
    private String id;
    private String email;

    public user(String name, String id, String email) {
        this.name = name;
        this.id = id;
        this.email = email;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public abstract void DisplayUserInfo();
}

// Interface for notifications
interface Notifiable {
    void sendEmail(String recipient, String subject, String message);
    void sendSMS(String recipient, String message);
}

// SMSNotification class implementing Notifiable
class SMSNotification implements Notifiable {
    @Override
    public void sendEmail(String recipient, String subject, String message) {
        System.out.println("Sending Email to: " + recipient);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
    }

    @Override
    public void sendSMS(String recipient, String message) {
        System.out.println("Sending SMS to: " + recipient);
        System.out.println("Message: " + message);
    }
}

// ReminderService class for sending reminders
class ReminderService {
    private Notifiable notificationService;

    public ReminderService(Notifiable notificationService) {
        this.notificationService = notificationService;
    }

    public void sendAppointmentReminder(String recipient, String appointmentDetails) {
        String subject = "Appointment Reminder";
        String message = "You have an upcoming appointment: " + appointmentDetails;
        notificationService.sendEmail(recipient, subject, message);
        notificationService.sendSMS(recipient, message);
    }

    public void sendMedicationReminder(String recipient, String medicationDetails) {
        String subject = "Medication Reminder";
        String message = "It's time to take your medication: " + medicationDetails;
        notificationService.sendEmail(recipient, subject, message);
        notificationService.sendSMS(recipient, message);
    }
}

// Patient class
class patient extends user {
    private ArrayList<VitalSign> vital;
    private ArrayList<Appointment> appointments;
    private ArrayList<Feedback> feedbackList;

    public patient(String name, String id, String email) {
        super(name, id, email);
        vital = new ArrayList<>();
        appointments = new ArrayList<>();
        feedbackList = new ArrayList<>();
    }

    public void uploadVital(VitalSign vitalSign) {
        vital.add(vitalSign);
    }

    public void DisplayUserInfo() {
        System.out.printf("The name of the patient is %s\nThe ID of the patient is %s\nThe email of the patient is %s\n", this.getName(), this.getId(), this.getEmail());
    }

    public void viewFeedback() {
        if (feedbackList.isEmpty()) {
            System.out.println("No feedback available");
        } else {
            for (Feedback feedback : feedbackList) {
                System.out.println("Doctor: " + feedback.getDoctorName());
                System.out.println("Feedback: " + feedback.getComment());
                System.out.println();
            }
        }
    }

    public void scheduleAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public ArrayList<VitalSign> getVitals() {
        return vital;
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    public ArrayList<Feedback> getFeedbackList() {
        return feedbackList;
    }

    public void addFeedback(Feedback feedback) {
        feedbackList.add(feedback);
    }

    public void requestAppointment(String appointmentId, String doctorId, LocalDateTime appointmentDate) {
        Appointment appointment = new Appointment(appointmentId, this.getId(), doctorId, appointmentDate);
        this.getAppointments().add(appointment);
        System.out.println("Appointment request submitted successfully.");
    }
}


// Doctor class
class Doctor extends user {
    private ArrayList<patient> patients;
    private ArrayList<Appointment> appointments;

    public Doctor(String id, String name, String email) {
        super(id, name, email);
        this.patients = new ArrayList<>();
        this.appointments = new ArrayList<>();
    }

    public void addPatient(patient patient) {
        patients.add(patient);
    }

    public void approveAppointment(String appointmentId) {
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                appointment.approveAppointment();
                System.out.println("Appointment with ID " + appointmentId + " has been approved.");
                return;
            }
        }
        System.out.println("Appointment with ID " + appointmentId + " not found.");
    }

    public void giveFeedback(patient patient, String comment) {
        Feedback feedback = new Feedback(comment, this.getName());
        patient.addFeedback(feedback);
    }

    public ArrayList<patient> getPatients() {
        return patients;
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    public void DisplayUserInfo() {
        System.out.printf("The name of the Doctor is %s\nThe ID of the doctor is %s\nThe email of the doctor is %s\n", this.getName(), this.getId(), this.getEmail());
    }
}

// Administrator class
class Administrator extends user {
    private ArrayList<Doctor> doctors;
    private ArrayList<patient> patients;
    private ArrayList<String> systemLogs;

    public Administrator(String id, String name, String email) {
        super(id, name, email);
        this.doctors = new ArrayList<>();
        this.patients = new ArrayList<>();
        this.systemLogs = new ArrayList<>();
    }

    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
        systemLogs.add("New doctor added: " + doctor.getName());
    }

    public void addPatient(patient patient) {
        patients.add(patient);
        systemLogs.add("New patient added: " + patient.getName());
    }

    public void viewSystemLogs() {
        if (systemLogs.isEmpty()) {
            System.out.println("No logs found");
        } else {
            for (String log : systemLogs) {
                System.out.println(log);
            }
        }
    }

    public ArrayList<Doctor> getDoctors() {
        return doctors;
    }

    public ArrayList<patient> getPatients() {
        return patients;
    }

    public ArrayList<String> getSystemLogs() {
        return systemLogs;
    }

    public void DisplayUserInfo() {
        System.out.printf("The name of the administrator is %s\nThe ID of the administrator is %s\nThe email of the administrator is %s\n", this.getName(), this.getId(), this.getEmail());
    }

    public void scheduleAppointment(String appointmentId, String patientId, String doctorId, LocalDateTime appointmentDate) {
        Appointment appointment = new Appointment(appointmentId, patientId, doctorId, appointmentDate);
        for (Doctor doctor : doctors) {
            if (doctor.getId().equals(doctorId)) {
                doctor.getAppointments().add(appointment);
                System.out.println("Appointment scheduled successfully for Doctor ID: " + doctorId);
                return;
            }
        }
        System.out.println("Doctor with ID " + doctorId + " not found.");
    }
}

// VitalSign class
class VitalSign {
    private int heartRate;
    private int oxygenLevel;
    private String bloodPressure;
    private double temperature;
    private LocalDateTime timestamp;

    public VitalSign(int heartRate, int oxygenLevel, String bloodPressure, double temperature) {
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.timestamp = LocalDateTime.now();
    }

    public int getHeartRate() {
        return heartRate;
    }

    public int getOxygenLevel() {
        return oxygenLevel;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public double getTemperature() {
        return temperature;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

// Appointment class
class Appointment {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDateTime appointmentDate;
    private boolean isApproved;

    public Appointment(String appointmentId, String patientId, String doctorId, LocalDateTime appointmentDate) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.isApproved = false;
    }

    public void approveAppointment() {
        this.isApproved = true;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public boolean isApproved() {
        return isApproved;
    }
}

// Feedback class
class Feedback {
    private String comment;
    private String doctorName;

    public Feedback(String comment, String doctorName) {
        this.comment = comment;
        this.doctorName = doctorName;
    }

    public String getComment() {
        return comment;
    }

    public String getDoctorName() {
        return doctorName;
    }
}

// PanicButton class
class PanicButton {
    public void triggerEmergency(patient patient) {
        System.out.println("PANIC BUTTON ACTIVATED by patient " + patient.getName());
        System.out.println("Emergency alert triggered!");
    }
}

// EmergencyAlert class
class EmergencyAlert {
    public void checkVitals(VitalSign vital, patient patient) {
        boolean alertTriggered = false;

        if (vital.getHeartRate() < 60 || vital.getHeartRate() > 100) {
            System.out.println("ALERT: Abnormal heart rate detected for patient " + patient.getName());
            alertTriggered = true;
        }
        if (vital.getOxygenLevel() < 90) {
            System.out.println("ALERT: Low oxygen level detected for patient " + patient.getName());
            alertTriggered = true;
        }
        if (vital.getTemperature() < 97.0 || vital.getTemperature() > 99.5) {
            System.out.println("ALERT: Abnormal temperature detected for patient " + patient.getName());
            alertTriggered = true;
        }

        if (alertTriggered) {
            System.out.println("Critical vitals detected! Please contact a doctor immediately.");
        } else {
            System.out.println("All vitals are within normal ranges.");
        }
    }
}

// NotificationService class
class NotificationService {
    public void sendAlert(patient patient, String message) {
        System.out.println("Sending alert to patient " + patient.getName() + " at " + patient.getEmail());
        System.out.println("Message: " + message);
    }

    public void sendAlertToDoctor(Doctor doctor, String message) {
        System.out.println("Sending alert to doctor " + doctor.getName() + " at " + doctor.getEmail());
        System.out.println("Message: " + message);
    }
}

// ChatServer class to handle doctor-patient chat messages
class ChatServer {
    private ArrayList<String> messages;

    public ChatServer() {
        messages = new ArrayList<>();
    }

    public void sendMessage(String sender, String message) {
        messages.add(sender + ": " + message);
    }

    public void displayMessages() {
        System.out.println("\n--- Chat Messages ---");
        for (String message : messages) {
            System.out.println(message);
        }
    }
}

// ChatClient class for doctor-patient chat interface
class ChatClient {
    private ChatServer chatServer;
    private String userName;

    public ChatClient(ChatServer chatServer, String userName) {
        this.chatServer = chatServer;
        this.userName = userName;
    }

    public void sendMessage(String message) {
        chatServer.sendMessage(userName, message);
    }

    public void viewChat() {
        chatServer.displayMessages();
    }
}

// VideoCall class to simulate video consultation
class VideoCall {
    public void startCall(String doctorName, String patientName) {
        System.out.println("\n--- Video Call Started ---");
        System.out.println("Doctor: " + doctorName);
        System.out.println("Patient: " + patientName);
        System.out.println("Google Meet/Zoom link: https://meet.google.com/example");
        System.out.println("Please join the video call using the above link.");
    }
}

// Main class
public class Test {
    static Administrator admin;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ChatServer chatServer = new ChatServer();
        SMSNotification smsNotification = new SMSNotification();
        ReminderService reminderService = new ReminderService(smsNotification);

        while (true) {
            try {
                System.out.println("\n--- Welcome to the Hospital Management System ---");
                System.out.println("Select your role:");
                System.out.println("1. Administrator");
                System.out.println("2. Doctor");
                System.out.println("3. Patient");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        handleAdministrator(scanner);
                        break;
                    case 2:
                        handleDoctor(scanner, chatServer);
                        break;
                    case 3:
                        handlePatient(scanner, chatServer, reminderService);
                        break;
                    case 4:
                        System.out.println("Exiting the system. Goodbye!");
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice! Please enter a number between 1 and 4.");
                }
            } catch (Exception e) {
                System.out.println("Error: Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    private static void handleAdministrator(Scanner scanner) {
        System.out.println("\n--- Administrator Menu ---");
        System.out.print("Enter your Name: ");
        String adminName = scanner.nextLine();
        System.out.print("Enter your ID: ");
        String adminId = scanner.nextLine();
        System.out.print("Enter your Email: ");
        String adminEmail = scanner.nextLine();

        admin = new Administrator(adminId, adminName, adminEmail);

        while (true) {
            try {
                System.out.println("\nWelcome, " + adminName + "!");
                System.out.println("1. Add Doctor");
                System.out.println("2. Add Patient");
                System.out.println("3. View System Logs");
                System.out.println("4. Back to Main Menu");
                System.out.print("Enter your choice: ");
                int adminChoice = scanner.nextInt();
                scanner.nextLine();

                switch (adminChoice) {
                    case 1:
                        System.out.println("\nEnter Doctor Details:");
                        System.out.print("Name: ");
                        String doctorName = scanner.nextLine();
                        System.out.print("ID: ");
                        String doctorId = scanner.nextLine();
                        System.out.print("Email: ");
                        String doctorEmail = scanner.nextLine();
                        Doctor newDoctor = new Doctor(doctorId, doctorName, doctorEmail);
                        admin.addDoctor(newDoctor);
                        System.out.println("Doctor added successfully!");
                        break;
                    case 2:
                        System.out.println("\nEnter Patient Details:");
                        System.out.print("Name: ");
                        String patientName = scanner.nextLine();
                        System.out.print("ID: ");
                        String patientId = scanner.nextLine();
                        System.out.print("Email: ");
                        String patientEmail = scanner.nextLine();
                        patient newPatient = new patient(patientName, patientId, patientEmail);
                        admin.addPatient(newPatient);
                        System.out.println("Patient added successfully!");
                        break;
                    case 3:
                        System.out.println("\n--- System Logs ---");
                        admin.viewSystemLogs();
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid choice! Please enter a number between 1 and 4.");
                }
            } catch (Exception e) {
                System.out.println("Error: Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    private static void handleDoctor(Scanner scanner, ChatServer chatServer) {
        System.out.println("\n--- Doctor Menu ---");
        System.out.print("Enter your Name: ");
        String doctorName = scanner.nextLine();
        System.out.print("Enter your ID: ");
        String doctorId = scanner.nextLine();
        System.out.print("Enter your Email: ");
        String doctorEmail = scanner.nextLine();
        Doctor doctor = new Doctor(doctorId, doctorName, doctorEmail);
        ChatClient doctorChatClient = new ChatClient(chatServer, doctorName);

        while (true) {
            try {
                System.out.println("\nWelcome, Dr. " + doctorName + "!");
                System.out.println("1. Approve Appointment");
                System.out.println("2. Give Feedback");
                System.out.println("3. View Patients");
                System.out.println("4. Chat with Patient");
                System.out.println("5. Start Video Call");
                System.out.println("6. Back to Main Menu");
                System.out.print("Enter your choice: ");
                int doctorChoice = scanner.nextInt();
                scanner.nextLine();

                switch (doctorChoice) {
                    case 1:
                        System.out.print("\nEnter Appointment ID to approve: ");
                        String appointmentId = scanner.nextLine();
                        doctor.approveAppointment(appointmentId);
                        break;
                    case 2:
                        System.out.print("\nEnter Patient ID to give feedback: ");
                        String patientId = scanner.nextLine();
                        System.out.print("Enter Feedback: ");
                        String feedbackComment = scanner.nextLine();
                        for (patient p : doctor.getPatients()) {
                            if (p.getId().equals(patientId)) {
                                doctor.giveFeedback(p, feedbackComment);
                                System.out.println("Feedback added successfully!");
                                break;
                            }
                        }
                        break;
                    case 3:
                        System.out.println("\n--- Patients List ---");
                        for (patient p : doctor.getPatients()) {
                            System.out.println("Patient Name: " + p.getName());
                            System.out.println("Patient ID: " + p.getId());
                            System.out.println("Patient Email: " + p.getEmail());
                            System.out.println();
                        }
                        break;
                    case 4:
                        System.out.print("\nEnter message to send to patient: ");
                        String message = scanner.nextLine();
                        doctorChatClient.sendMessage(message);
                        break;
                    case 5:
                        System.out.print("\nEnter Patient Name for Video Call: ");
                        String patientNameForCall = scanner.nextLine();
                        VideoCall videoCall = new VideoCall();
                        videoCall.startCall(doctorName, patientNameForCall);
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("Invalid choice! Please enter a number between 1 and 6.");
                }
            } catch (Exception e) {
                System.out.println("Error: Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    private static void handlePatient(Scanner scanner, ChatServer chatServer, ReminderService reminderService) {
        System.out.println("\n--- Patient Menu ---");
        System.out.print("Enter your ID: ");
        String patientId = scanner.nextLine();
        System.out.print("Enter your Email: ");
        String patientEmail = scanner.nextLine();
        patient patient = null;

        // Ensure the patient exists (this assumes the Administrator has already added the patient)
        for (patient p : admin.getPatients()) {
            if (p.getId().equals(patientId) && p.getEmail().equals(patientEmail)) {
                patient = p;
                break;
            }
        }

        if (patient == null) {
            System.out.println("Patient not found. Please contact the Administrator to register.");
            return;
        }

        ChatClient patientChatClient = new ChatClient(chatServer, patient.getName());

        while (true) {
            try {
                System.out.println("\nWelcome, " + patient.getName() + "!");
                System.out.println("1. View Feedback");
                System.out.println("2. View Appointments");
                System.out.println("3. Upload Vital Signs");
                System.out.println("4. Chat with Doctor");
                System.out.println("5. Trigger Emergency Alerts");
                System.out.println("6. Set Appointment Reminder");
                System.out.println("7. Set Medication Reminder");
                System.out.println("8. Request Appointment");
                System.out.println("9. Back to Main Menu");
                System.out.print("Enter your choice: ");
                int patientChoice = scanner.nextInt();
                scanner.nextLine();

                switch (patientChoice) {
                    case 1:
                        System.out.println("\n--- Feedback ---");
                        patient.viewFeedback();
                        break;
                    case 2:
                        System.out.println("\n--- Appointments ---");
                        for (Appointment appointment : patient.getAppointments()) {
                            System.out.println("Appointment ID: " + appointment.getAppointmentId());
                            System.out.println("Doctor ID: " + appointment.getDoctorId());
                            System.out.println("Date: " + appointment.getAppointmentDate());
                            System.out.println("Status: " + (appointment.isApproved() ? "Approved" : "Pending"));
                            System.out.println();
                        }
                        break;
                    case 3:
                        System.out.println("\nEnter Vital Signs:");
                        System.out.print("Heart Rate: ");
                        int heartRate = scanner.nextInt();
                        System.out.print("Oxygen Level: ");
                        int oxygenLevel = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Blood Pressure: ");
                        String bloodPressure = scanner.nextLine();
                        System.out.print("Temperature: ");
                        double temperature = scanner.nextDouble();
                        VitalSign vitalSign = new VitalSign(heartRate, oxygenLevel, bloodPressure, temperature);
                        patient.uploadVital(vitalSign);
                        System.out.println("Vital signs uploaded successfully!");
                        break;
                    case 4:
                        System.out.print("\nEnter message to send to doctor: ");
                        String message = scanner.nextLine();
                        patientChatClient.sendMessage(message);
                        break;
                    case 5:
                        System.out.println("\n--- Trigger Emergency Alert ---");
                        if (patient.getVitals().isEmpty()) {
                            System.out.println("No vital signs available to check. Please upload vital signs first.");
                        } else {
                            EmergencyAlert emergencyAlert = new EmergencyAlert();
                            for (VitalSign vital : patient.getVitals()) {
                                emergencyAlert.checkVitals(vital, patient);
                            }
                        }
                        break;
                    case 6:
                        System.out.print("\nEnter Appointment Details: ");
                        String appointmentDetails = scanner.nextLine();
                        reminderService.sendAppointmentReminder(patient.getEmail(), appointmentDetails);
                        break;
                    case 7:
                        System.out.print("\nEnter Medication Details: ");
                        String medicationDetails = scanner.nextLine();
                        reminderService.sendMedicationReminder(patient.getEmail(), medicationDetails);
                        break;
                    case 8:
                        System.out.println("\n--- Request Appointment ---");
                        System.out.print("Enter Appointment ID: ");
                        String appointmentId = scanner.nextLine();
                        System.out.print("Enter Doctor ID: ");
                        String doctorId = scanner.nextLine();
                        System.out.print("Enter Appointment Date (YYYY-MM-DD HH:MM): ");
                        String dateInput = scanner.nextLine();
                        try {
                            LocalDateTime appointmentDate = LocalDateTime.parse(dateInput.replace(" ", "T"));
                            patient.requestAppointment(appointmentId, doctorId, appointmentDate);
                        } catch (Exception e) {
                            System.out.println("Error: Invalid date format. Please use YYYY-MM-DD HH:MM.");
                        }
                        break;
                    case 9:
                        return;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }
}