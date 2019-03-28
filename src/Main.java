import java.util.HashSet;
import java.util.Random;

public class Main
{
	public static void main(String[] args)
	{
		final String usage = "Usage: java Main random|local";
		
		// Usage: WormProp random|local
		if (args.length != 1)
		{
			System.out.println(usage);
			return;
		}
		
		WormProp worm = null;
		
		if (args[0].equals("random"))
		{
			worm = new RandomWormProp();
		}
		else if (args[0].equals("local"))
		{
			worm = new LocalWormProp();
		}
		else
		{
			System.out.println(usage);
			return;
		}
		
		worm.Run();
	}
	
	public static abstract class WormProp
	{		
		protected int _NumIps;
		private int _NumComputers;
		private HashSet<Integer> _InfectedComputers;
		private int _ScanRate;
		protected Random _Random;
		
		public WormProp()
		{
			_NumIps = 100000;
			_NumComputers = 1000;
			_InfectedComputers = new HashSet<Integer>();
			_ScanRate = 3;
			_Random = new Random();
			
			// Assignment indicates to infect Machine 1000
			_InfectedComputers.add(1000);
		}
		
		public void Run()
		{
			int timeTick = 0;
			
			// Iterate until all computers have been infected
			while (_InfectedComputers.size() < _NumComputers)
			{
				// Statistics
				System.out.println(timeTick + ", " + _InfectedComputers.size());
				
				HashSet<Integer> newlyInfectedComputers = new HashSet<Integer>();
				
				// Iterate for each infected computer
				for (Integer infectedIp : _InfectedComputers)
				{
					// Scan Rate, the number of IPs to choose
					for (int scan = 0; scan < _ScanRate; ++scan)
					{
						int ip = SelectIp(infectedIp);
						
						// Only infect if the computer is vulnerable AND has not been infected already
						if (IsVulnerable(ip) && !_InfectedComputers.contains(ip) && !newlyInfectedComputers.contains(ip))
						{
							newlyInfectedComputers.add(ip);
						}
					}
				}
				
				// Add the computers infected in this time step
				_InfectedComputers.addAll(newlyInfectedComputers);
				
				// Time
				++timeTick;
			}

			// Statistics
			System.out.println(timeTick + ", " + _InfectedComputers.size());
		}
		
		/**
		 * Selects a random IP to scan.
		 * Children will override this to implement worm behavior
		 * @param currentIp The IP of the scanning computer
		 * @return The IP chosen to scan 
		 */
		protected abstract int SelectIp(int currentIp);
		
		/**
		 * Helper function to determine if the given IP is vulnerable.
		 * Rules for vulnerability given by assignment.
		 * @param ip The IP Address to check
		 * @return True if the IP Address is vulnerable; false, otherwise
		 */
		private boolean IsVulnerable(int ip)
		{
			// Vulnerable computers have the following specific IP addresses:
			// 0, 1, 2, ..., 9,
			// 1000, 1001, ..., 1009,
			// 2000, 2001, ..., 2009,
			// ...
			return ip >= 0 && ip < _NumIps && (ip % 1000) < 10;
		}
	}
	
	public static class RandomWormProp extends WormProp
	{
		/**
		 * Selects a random IP to scan
		 * @param currentIp The IP of the scanning computer
		 * @return A random IP in the range of [0, NumIps) 
		 */
		@Override
		protected int SelectIp(int currentIp)
		{
			return _Random.nextInt(_NumIps);
		}
	}
	
	public static class LocalWormProp extends WormProp
	{
		/**
		 * Selects a random IP to scan with local preference
		 * @param currentIp The IP of the scanning computer
		 * @return A random IP in the range of [0, NumIps) 
		 */
		@Override
		protected int SelectIp(int currentIp)
		{	
			// Returns a random int on [0, 9]
			int randomRoll = _Random.nextInt(10);
			
			//  (1). With probability p = 0.8, it picks a random value y such that  y E [x-10, x+10]
			if (randomRoll < 8)
			{
				return Math.floorMod(currentIp - 10 + _Random.nextInt(21), _NumIps);
			}
			
			//  (2). With the remaining probability 0.2, it picks a random IP value y between 1 to 100,000.
			return _Random.nextInt(_NumIps);
		}
		
	}
}
