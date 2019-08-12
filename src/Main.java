import java.io.File;
import java.util.*;

public class Main {

	static Hashtable<String, Team> teams = new Hashtable<>();
	static final int MATCH_LENGTH = 4;
	static final int[][] scores = {{4,0},{3,1},{3,2},{2,3},{1,3},{0,4}};
	static int it = 1;

	public static void main(String[] args){
		Scanner scan = null;

		try {
			scan = new Scanner(new File("results.txt"));
		}catch(Exception e){
			e.printStackTrace();
		}

		while(scan.hasNextLine()){
			String line = scan.nextLine();
			String[] lin = line.split("[- ]");

			Team team1 = teams.get(lin[0]);
			if(team1 == null){
				team1 = new Team(lin[0]);
				teams.put(lin[0], team1);
			}

			Team team2 = teams.get(lin[3]);
			if(team2 == null){
				team2 = new Team(lin[3]);
				teams.put(lin[3], team2);
			}

			int t1w = Integer.parseInt(lin[1]);
			int t2w = Integer.parseInt(lin[2]);
			int drw = Math.max(0, MATCH_LENGTH-t1w-t2w);

			team1.result(t1w, t2w, drw);
			team2.result(t2w, t1w, drw);

		}

		ArrayList<Team> rankings = new ArrayList<Team>(20);

		for(String t : teams.keySet()){
			//System.out.println(teams.get(t));
			rankings.add(teams.get(t));

		}

		rankings.sort(new TeamComparator<>());

		LinkedList<String> games = new LinkedList<>();

		try {
			scan.close();
			scan = new Scanner(new File("remaining.txt"));
		}catch(Exception e){
			e.printStackTrace();
		}

		while(scan.hasNextLine()){
			games.add(scan.nextLine());
		}

		try {
			scan.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		while(it<=10000000){
			weight_rand_branch(games, teams);
		}

		for(Team t : rankings){
			System.out.println(t.getName()+" "+Arrays.toString(t.rank));
		}

	}

	public static void weight_rand_branch(LinkedList<String> games, Hashtable<String, Team> teamss){
		LinkedList<String> games_copy = (LinkedList<String>) games	.clone();

		Hashtable<String, Team> teams_copy = new Hashtable<>();
		for(String t : teamss.keySet()){
			teams_copy.put(t,teamss.get(t).clone());
		}

		while(!games_copy.isEmpty()){
			String[] game = games_copy.poll().split(" ");

			double wr1 = teams.get(game[0]).winrate();
			double wr2 = teams.get(game[1]).winrate();

			double fact = wr1*0.5/(wr1+wr2);
			double x = fact*(new Random()).nextDouble();

			int[] i = scores[(int)(x*(scores.length))];

			Team team1 = teams_copy.get(game[0]);
			Team team2 = teams_copy.get(game[1]);
			team1.result(i[0], i[1], 0);
			team2.result(i[1], i[0], 0);
		}

		ArrayList<Team> rankings = new ArrayList<Team>();
		for(String t : teams_copy.keySet()){
			rankings.add(teams_copy.get(t));
		}
		rankings.sort(new TeamComparator<>());

		for(int i=0; i<rankings.size(); i++){
			String tm = rankings.get(i).getName();
			teams.get(tm).rank[i]++;
		}

		if(it%10000==0)System.out.println("Iteration "+it+" complete.");
		it++;
		return;
	}

	public static void rand_branch(LinkedList<String> games, Hashtable<String, Team> teamss){
		LinkedList<String> games_copy = (LinkedList<String>) games	.clone();

		Hashtable<String, Team> teams_copy = new Hashtable<>();
		for(String t : teamss.keySet()){
			teams_copy.put(t,teamss.get(t).clone());
		}

		while(!games_copy.isEmpty()){
			String[] game = games_copy.poll().split(" ");

			int[] i = scores[(new Random()).nextInt(scores.length)];

			Team team1 = teams_copy.get(game[0]);
			Team team2 = teams_copy.get(game[1]);
			team1.result(i[0], i[1], 0);
			team2.result(i[1], i[0], 0);
		}

		ArrayList<Team> rankings = new ArrayList<Team>();
		for(String t : teams_copy.keySet()){
			rankings.add(teams_copy.get(t));
		}
		rankings.sort(new TeamComparator<>());

		for(int i=0; i<rankings.size(); i++){
			String tm = rankings.get(i).getName();
			teams.get(tm).rank[i]++;
		}

		if(it%10000==0)System.out.println("Iteration "+it+" complete.");
		it++;
		return;

	}

	public static void rec_branch(LinkedList<String> games, Hashtable<String, Team> teamss){
		if(games.isEmpty()){
			LinkedList<Team> rankings = new LinkedList<Team>();
			for(String t : teamss.keySet()){
				rankings.add(teamss.get(t).clone());
			}
			rankings.sort(new TeamComparator<>());
			for(int i=0; i<rankings.size(); i++){
				String t = rankings.poll().getName();
				teams.get(t).rank[i]++;
			}
			if(it%10000==0)System.out.println("Iteration "+it+" complete.");
			it++;
			return;
		}

		LinkedList<String> games_copy = (LinkedList<String>) games	.clone();
		String[] game = games_copy.poll().split(" ");
		for(int[] i : scores){
			Hashtable<String, Team> teams_copy = new Hashtable<>();
			for(String t : teamss.keySet()){
				teams_copy.put(t,teamss.get(t));
			}

			Team team1 = teams_copy.get(game[0]);
			Team team2 = teams_copy.get(game[1]);
			team1.result(i[0], i[1], 0);
			team2.result(i[1], i[0], 0);
			rec_branch(games_copy, teams_copy);
		}

	}

}

class TeamComparator<Team> implements Comparator<Team>{

	@Override
	public int compare(Team o1, Team o2) {
		String[] t1 = o1.toString().split("[ ()-]");
		String[] t2 = o2.toString().split("[ ()-]");

		double t1wr = Integer.parseInt(t1[1])*1.0/(Integer.parseInt(t1[1])+Integer.parseInt(t1[2])+Integer.parseInt(t1[3]));
		double t2wr = Integer.parseInt(t2[1])*1.0/(Integer.parseInt(t2[1])+Integer.parseInt(t2[2])+Integer.parseInt(t2[3]));
		int t1md = Integer.parseInt(t1[5])-Integer.parseInt(t1[6]);
		int t2md = Integer.parseInt(t2[5])-Integer.parseInt(t2[6]);

		//System.out.print(t1[0] + " "+t1wr+" "+t1md+" vs "+t2[0]+" "+ t2wr+" "+t2md+" ");

		if(t1wr>t2wr||(t1wr==t2wr&&t1md>t2md)){
			return -1;
		}

		if(t1wr<t2wr||(t1wr==t2wr&&t1md<t2md)){
			return 1;
		}

		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}
}
