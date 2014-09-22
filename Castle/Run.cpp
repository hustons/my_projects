#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>

using namespace std;

bool DEBUG = false;

class Cannon{
	public:
		int BallsFired;
		Cannon(int id);
		void Reset();
		void Assassinate();
		bool Fire();
		
	private:
		bool Alive;
		int Id;
};

class Castle 
{
	private:
		vector<int> Targets;
		vector<Cannon> Cannons;
		int NumCannons;
		void InitCannons();
		void ResetCannons();
		void FireCannons(int cannonPosition);
		int GetTotalBallsFired();
	
	public:
		int MaxBallsFired;
		Castle(int numCannons, vector<int> targets);
		void RunTestCase();
		void PrintTargets();
};

class InputData
{
	private:
		int NumCases;
		vector<int> NumCannons; // One entry for each test case
		vector<int> NumTargets; // One entry for each test case
		vector< vector<int> > Targets; // One vector for each test case
	
	public:
		InputData(char* path);
		void Run();
};

Cannon::Cannon(int id)
{
	Alive = true;
	BallsFired = 0;
	Id = id;
}
	
void Cannon::Reset()
{
	Alive = true;
	BallsFired = 0;
}

void Cannon::Assassinate()
{
	if (DEBUG) cout << "Cannon " << Id << " is dead!" << endl;
	Alive = false;
}

bool Cannon::Fire()
{
	if (!Alive)
	{
		if (DEBUG) cout << "Cannon " << Id << " cannot fire since it is dead!" << endl;
		return false;
	}
	
	if (DEBUG) cout << "Firing cannon " << Id << endl;
	BallsFired++;
	return true;
}

// Setup the cannons
void Castle::InitCannons()
{
	for (int i=0; i < NumCannons; i++)
	{
		Cannon cannon(i+1);
		Cannons.push_back(cannon);
	}
}

// Reset cannons for the next evaluation
void Castle::ResetCannons()
{
	for (vector<Cannon>::iterator it = Cannons.begin(); 
			it!=Cannons.end(); ++it)
	{
		it->Reset();
	}
}

void Castle::FireCannons(int cannonPosition)
{
	//Fire cannons to the right of the target
	for (vector<Cannon>::iterator it = Cannons.begin() + cannonPosition; 
			it!=Cannons.end(); ++it)
	{
		 if (!it->Fire()) break;			 
	}
	
	// Fire cannons to the left of the target
	for (vector<Cannon>::reverse_iterator rit = 
			Cannons.rbegin() + NumCannons + 1 - cannonPosition; 
			rit!=Cannons.rend(); ++rit)
	{
		if (!rit->Fire()) break;			 
	}
}

int Castle::GetTotalBallsFired()
{
	int total = 0;
	for (vector<Cannon>::iterator it = Cannons.begin(); 
			it!=Cannons.end(); ++it)
	{
		 total += it->BallsFired;			 
	}
	if (DEBUG) cout << "Total balls fired: " << total << endl;
	return total;
}

Castle::Castle(int numCannons, vector<int> targets)
{
	NumCannons = numCannons;
	Targets = targets;
	InitCannons();
	PrintTargets();
}

void Castle::RunTestCase()
{
	MaxBallsFired = 0;
	// Loop over all permutations of Targets
	do {
		ResetCannons();
		for (vector<int>::iterator it = Targets.begin(); 
				it!=Targets.end(); ++it)
		{
			Cannons.at(*it - 1).Assassinate();
			FireCannons(*it);		
		}
		
		int thisTotal = GetTotalBallsFired();
		if (thisTotal > MaxBallsFired)
		{
			if (DEBUG) cout << "Updating MaxBallsFired from " 
							<< MaxBallsFired << " to " 
							<< thisTotal << endl; 
			MaxBallsFired = thisTotal;
		}
		
		if (DEBUG) cout << "--- End of permuation ---" << endl;
		
	} while (next_permutation(Targets.begin(), Targets.end()));
}
		
void Castle::PrintTargets()
{
	if (!DEBUG) return;
	
	for (vector<int>::iterator it = Targets.begin(); 
			it!=Targets.end(); ++it)
	{
		cout << "Target: " << *it << endl;
	}
}

InputData::InputData(char* path)
{
	ifstream reader;
	reader.open(path);
	
	reader >> NumCases;
	if (DEBUG) cout << "Have found " << NumCases << " cases." << endl;
	
	for (int i = 0; i < NumCases; i++)
	{
		int numTargets = 0, numCannons = 0;
		if (DEBUG) cout << "Reading case " << i + 1 << endl;
		reader >> numCannons >> numTargets;
		NumCannons.push_back(numCannons);
		NumTargets.push_back(numTargets);
		if (DEBUG) cout << "NumCannons: " << numCannons 
						<< " NumTargets: " << numTargets 
						<< endl;		
			
		vector<int> targets;			
		for (int j = 0; j < numTargets; j++)
		{
			int cannon;
			reader >> cannon;
			targets.push_back(cannon);
		}
		Targets.push_back(targets);
	}
	
	reader.close();
}

void InputData::Run()
{
	for (int i = 0; i < NumCases; i++)
	{
		Castle castle(NumCannons.at(i), Targets.at(i));
		castle.RunTestCase();
		cout << "Case " << i+1 << ": " 
			 << castle.MaxBallsFired << endl;		
	}
}

int main( int argc, char* argv[] )
{
	InputData input(argv[1]);
	input.Run();	
	return 0;
}

