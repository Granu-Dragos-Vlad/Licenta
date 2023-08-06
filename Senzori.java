import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import com.google.api.client.util.Objects;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import jssc.SerialPort;

import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Senzori
{
	public String[] split()
	{
		Random random=new Random();
		String[] tokenuri=new String[2];
	    int puls=random.nextInt(41) + 60;
		String act = random.nextBoolean() ? "true" : "false";
		tokenuri[0]=String.valueOf(puls);
		tokenuri[1]=act;
		  
		return tokenuri;
	}
	public void connect(String portname)
	{
		// Creez o instanta a clasei SerialPort si initializez o conexiune seriala
		final SerialPort port= new SerialPort(portname);
		 if (port.isOpened())
		 {
	            try {
					port.removeEventListener();
				} catch (SerialPortException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            try {
					port.closePort();
				} catch (SerialPortException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            System.out.println("Portul serial a fost închis.");
	    }
		// Ma conectez la portul specific si tratez cazul de eroare printr-o structura try-catch
		 try {
			    port.openPort();
			    // Setez parametrii portului
			    // BAUDRATE = viteza de transmitere a datelor, exprimata in BPS
			    // DATABITS = numarul de biti de date trimisi in fiecare cadru (5,6,7 sau 8)
			    // STOPBITS = numarul de biti de stop utilizati in fiecare cadru (1 sau 2)
			    // PARITY = o metoda de detectie a erorilor de transmisie
			    port.setParams(
			            SerialPort.BAUDRATE_9600,
			            SerialPort.DATABITS_8,
			            SerialPort.STOPBITS_1,
			            SerialPort.PARITY_NONE
			    );
			    // addEventListener() imi permite sa definesc o functie care se apeleaza automat atunci cand are loc
			    // un eveniment pe portul serial (primirea de date sau aparitia unei erori)
			   
			    port.addEventListener(new SerialPortEventListener() {
			        public void serialEvent(SerialPortEvent event) {
			        	 try {
			                 String s = port.readString(event.getEventValue());
			                 String [] tokens=split();
					         port.closePort();
			                 
			             } catch (SerialPortException e) {
			                 e.printStackTrace();
			             }
			           
			        }
			    });
			} catch (SerialPortException e) {
			    e.printStackTrace();
			}
	}
	public static void main(String []args) throws IOException, InterruptedException, ExecutionException
	{
		Senzori obj=new Senzori();
	    URL url =obj.getClass().getClassLoader().getResource("serviceAccountKey.json");
	    File file = new File(url.getFile());
		FileInputStream serviceAccount = new FileInputStream(file.getAbsolutePath());
		FirebaseOptions options = new FirebaseOptions.Builder()
				  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
				  .build();
		FirebaseApp.initializeApp(options);
		// Obtinem o lista de siruri de caractere a porturilor disponibile pentru conectare
		String portlist[]= SerialPortList.getPortNames();
		// Ma conectez la portul Arduino specific(COM5)
		obj.connect(portlist[portlist.length-1]);
		Firestore dbfirestore= FirestoreClient.getFirestore();
		// Obtin referința colectiei 
		CollectionReference userdataRef = dbfirestore.collection("pacienti");
		// Realizez interogarea pentru a obtine toti utilizatorii
		ApiFuture<QuerySnapshot> query = userdataRef.get();
		// Astept obtinerea rezultatelor
		QuerySnapshot querySnapshot = query.get();
		// O lista cu toti utilizatorii
		List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
		// Obtin datele pentru user
		int id=documents.get(documents.size()-1).getLong("id").intValue()+1;
		String[] tokens=obj.split();
		int puls=Integer.parseInt(tokens[0]);
		boolean activ=Boolean.valueOf(tokens[1]);
		String numedocument="pacient"+String.valueOf(id);
		LocalDate currentDate = LocalDate.now();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	    String datastr = currentDate.format(formatter);
		// Creez un nou utilizator
		Pacient u=new Pacient(id,puls,activ,datastr);
		// il salvez in baza de date
		ApiFuture<WriteResult> collectionsApiFuture=dbfirestore.collection("pacienti").document(numedocument).set(u);
		System.out.println("Pacientul cu id-ul "+id+" a fost adaugat cu succes!");
		// Adaug utilizatorul in lista de pacienti a unui medic
		CollectionReference mediciRef=dbfirestore.collection("medici");
		// Realizez interogarea pentru a obtine toti medicii
		ApiFuture<QuerySnapshot> query2 = mediciRef.get();
		// Astept obtinerea rezultatelor
	    QuerySnapshot querySnapshot2 = query2.get();
		// O lista cu toti medicii
		List<QueryDocumentSnapshot> documents2 = querySnapshot2.getDocuments();
		// ii asignez un medic 
		Random random=new Random();
		int idmedic=random.nextInt(documents2.size())+1;
		for(QueryDocumentSnapshot document:documents2)
		{
		    DocumentReference medicRef = document.getReference();
			// Obțin datele medicului
		    Medic medic = document.toObject(Medic.class);
		    if(medic.getId()==2)
		    {
		    	// Adaug utilizatorul in lista de pacienti a medicului
		    	String lista=medic.getListapac();
		    	lista=lista+","+String.valueOf(id);
		    	medic.setListapac(lista);
		    	// fac update-ul in baza de date
		    	ApiFuture<WriteResult> collectionsApiFuture2=medicRef.set(medic);
		    	System.out.println("Pacientul cu id-ul "+id+" a fost asignat unui medic!");
		    }
		}
	}
}
