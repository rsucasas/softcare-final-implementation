package eu.ehealth.db.wservices.measurements;

import java.util.ArrayList;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import eu.ehealth.Globals;
import eu.ehealth.StorageComponentMain;
import eu.ehealth.db.DbStorageComponent;
import eu.ehealth.db.xsd.OperationResult;
import eu.ehealth.util.NullChecker;


/**
 * 
 * 
 * @author a572832
 * @date 19/03/2014.
 *
 */
public class DeletePatientAssessment extends DbStorageComponent<OperationResult, String>
{

	
	public DeletePatientAssessment(Session session)
	{
		super(session);
	}

	
	@Override
	protected OperationResult dbStorageFunction(ArrayList<String> lo)
	{
		try
		{
			String assessmentIdv = (String) lo.get(0); 
			String userId = (String) lo.get(1);
			
			// DEBUG - TRACE
			StorageComponentMain.scLog("DEBUG", "METHOD : DeletePatientAssessment");	
			
			OperationResult res = new OperationResult();

			NullChecker nc = new NullChecker();

			userId = nc.check(userId, String.class);
			assessmentIdv = nc.check(assessmentIdv, String.class);

			if (!checkUserPermissions("uploadData", userId, U_CLINICIAN, U_CARER, U_ADMIN))
			{
				// set OperationResult values
				return Globals.getOpResult(Globals.ResponseCode.PERMISSION_ERROR.getCode(), "");
			}

			try
			{
				_session.getTransaction().begin();

				Integer assessmentId = new Integer(assessmentIdv);
				_session.createSQLQuery("DELETE FROM measurement WHERE patientassessment = " + assessmentId.toString()).executeUpdate();
				_session.createSQLQuery("DELETE FROM patientassessment WHERE id = " + assessmentId.toString()).executeUpdate();

				_session.getTransaction().commit();

				// set OperationResult values
				res = Globals.getOpResult("" + assessmentId.toString(), "");
			}
			catch (HibernateException e)
			{
				rollbackSession();

				StorageComponentMain.logException(e);

				// set OperationResult values
				res = Globals.getOpResult(Globals.ResponseCode.DATABASE_ERROR_1.getCode(), " : " + e.toString());
			}

			return res;
		}
		catch (Exception ex)
		{
			StorageComponentMain.logException(ex);
			// set OperationResult values
			return Globals.getOpResult(Globals.ResponseCode.UNKNOWN_ERROR.getCode(), " : " + ex.toString());
		}
	}
	

}
