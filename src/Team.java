public class Team {

	public String name;
	public int wins, subwins, losses, sublosses, draws, subdraws;
	public long[] rank = new long[20];

	public Team(String name){
		this.name = name;
		wins = 0;
		subwins = 0;
		losses = 0;
		sublosses = 0;
		draws = 0;
		subdraws = 0;
	}

	public void result(int fsubwins, int esubwins, int subdraws){
		subwins+=fsubwins;
		sublosses+=esubwins;
		this.subdraws+=subdraws;
		if(fsubwins>esubwins)wins++;
		else if(fsubwins<esubwins)losses++;
		else draws++;
	}

	public double winrate(){
		return (wins+0.5*draws)/(wins+draws+losses);
	}

	public double subwinrate(){
		return (subwins+0.5*subdraws)/(subwins+subdraws+sublosses);
	}

	public int windiff(){
		return wins-losses;
	}

	public int subwindiff(){
		return subwins-sublosses;
	}

	public String getName(){
		return name;
	}

	public String toString(){
		return name + " " + wins + "-" + losses + "-" + draws + " ("+subwins+"-"+sublosses+"-"+subdraws+")";
	}

	public Team clone(){
		Team ret = new Team(this.name);
		ret.wins = this.wins;
		ret.subwins = this.subwins;
		ret.losses = this.losses;
		ret.sublosses = this.sublosses;
		ret.draws = this.draws;
		ret.subdraws = this.subdraws;
		ret.rank = this.rank.clone();
		return ret;
	}

}
