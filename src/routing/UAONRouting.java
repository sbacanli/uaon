package routing;

public class UAONRouting extends Probabilistic{
	boolean encounterHistoryExchange;
	
	public UAONRouting(Air ag, double prob,boolean enc) {
		super(ag, prob);
		encounterHistoryExchange=enc;
	}
	
	@Override
	public void send(String timet) {
		if(isOKToSend()) {
			if(encounterHistoryExchange) {
				getSender().sendEncounterHistory(getAir(), getReceiver().getId(), timet);
				getReceiver().receiveEncounterHistory(getAir(), timet);
			}
		}
		
		if(isOKToSend()) {
			if(encounterHistoryExchange) {
				getReceiver().sendEncounterHistory(getAir(), getSender().getId(), timet);
				getSender().receiveEncounterHistory(getAir(), timet);
			}
		}
		
		if(isOKToSend()){
			//exchanging encounter history information			
			communicate(getSender(),getReceiver(),timet);
		}
		if(isOKToSend()){
			communicate(getReceiver(),getSender(),timet);
		}
	}

}
