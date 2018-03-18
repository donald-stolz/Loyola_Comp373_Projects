package use;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import facility.FacilityDetailImpl;

public class UseImpl extends FacilityDetailImpl implements Use {

	private List<UseDetail> usageHistory;
	private List<Inspection> inspections;
	private final SimpleDateFormat sdf;


	public UseImpl() {
		this.sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}

	public void setUseDetails(List<UseDetail> useDetails) { usageHistory = useDetails; }
	public List<UseDetail> getUseDetails() { return usageHistory; }


	public boolean objectIsInUseDuringInterval(String start, String end) throws ParseException{
		Date checkStart = sdf.parse(start);
		Date checkEnd   = sdf.parse(end);

		for (UseDetail reservation: usageHistory) {
			Date reservationEnd = sdf.parse(reservation.getEnd());
			Date reservationStart = sdf.parse(reservation.getStart());

			if ((checkStart.compareTo(reservationStart) >= 0 && checkStart.compareTo(reservationEnd) <= 0) ||
					(checkEnd.compareTo(reservationStart) >= 0 && checkEnd.compareTo(reservationEnd) <= 0) ||
					(checkStart.compareTo(reservationStart) <= 0 && checkEnd.compareTo(reservationEnd) >= 0) ||
					(checkStart.compareTo(reservationStart) >= 0 && checkEnd.compareTo(reservationEnd) <= 0)) {
				return true;
			}
		}
		return false;
	}

	public void assignFacilityToUse(UseDetail reservation) throws ParseException{
		if (objectIsInUseDuringInterval(reservation.getStart(), reservation.getEnd())){
			System.out.println("Space is in use");
			return;
		}
		this.usageHistory.add(reservation);
	}

	public void removeObjectsDuringInterval(String start, String end) throws ParseException{
		Date checkStart = sdf.parse(start);
		Date checkEnd		= sdf.parse(end);
		for (int i = 0; i < usageHistory.size(); i++) {
			UseDetail reservation = usageHistory.get(i);

			Date reservationEnd = sdf.parse(reservation.getEnd());
			Date reservationStart = sdf.parse(reservation.getStart());

			if ((checkStart.compareTo(reservationStart) >= 0 && checkStart.compareTo(reservationEnd) <= 0) ||
					(checkEnd.compareTo(reservationStart) >= 0 && checkEnd.compareTo(reservationEnd) <= 0) ||
					(checkStart.compareTo(reservationStart) <= 0 && checkEnd.compareTo(reservationEnd) >= 0) ||
					(checkStart.compareTo(reservationStart) >= 0 && checkEnd.compareTo(reservationEnd) <= 0)) {
				this.usageHistory.remove(i);
			}
		}
	}

	//TODO: take in Inspection as argument, don't create one
	public void performInspection(String start, String end, String inspector, String info){
		Inspection inspect = new InspectionImpl();
		inspect.setStart(start);
		inspect.setEnd(end);
		inspect.setInspector(inspector);
		inspect.setInfo(info);
		this.inspections.add(inspect);
	}

	public List<Inspection> listInspections(){ return this.inspections; }

	public List<UseDetail> listActualUsage(){ return this.usageHistory; }

	public double calcUsageRate() throws ParseException{
		long useTime = 0;
		for (UseDetail use : this.listActualUsage()) {
			useTime += use.getDuration();
		}

		double usePerRes = useTime / (double) (this.listActualUsage().size());
		return usePerRes;
	}
}