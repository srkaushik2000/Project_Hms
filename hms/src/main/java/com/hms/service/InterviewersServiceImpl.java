package com.hms.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.comparators.ExperienceComparator;
import com.hms.dao.CandidatesDAO;
import com.hms.dao.InterviewersDAO;
import com.hms.entities.Candidates;
import com.hms.entities.Interviewer;

@Service
@Transactional(readOnly = true)
public class InterviewersServiceImpl implements InterviewersService {

	@Autowired
	private InterviewersDAO dao;

	@Autowired
	private CandidatesDAO candao;

	@Override
	public Interviewer findInterviewerById(int id, String availability) {
		Interviewer interviewer = dao.findInterviewerById(id, availability);
		return interviewer;
	}

	@Override
	public List<Interviewer> findAllInterviewers() {
		return dao.findAllInterviewers();
	}

	@Override
	@Transactional
	public void addInterviewer(Interviewer interviewer) {
		dao.addInterviewer(interviewer);
	}

	@Override
	@Transactional
	public Interviewer UpdateInterviewer(Interviewer interviewer, int candId) {
		Interviewer i = findInterviewerById(interviewer.getInterviewerIdentity().getInterviewers_id(),
				interviewer.getInterviewerIdentity().getAvailability());
		Candidates c = candao.findCandidateById(candId);
		if (i == null || c == null) {
			throw new IllegalArgumentException("Id not Found ");
		}
		i.setStatus("Y");
		c.setStatus("Y");
		c.setInterviewer(i);
		return dao.updateInterviewer(i, c);
	}

	@Override
	public List<Interviewer> preferredInterviewers(int id) {

		List<Interviewer> interviewersList = findAllInterviewers();
		Candidates candidates = candao.findCandidateById(id);
		List<String> candidatesSkills = Arrays.asList(candidates.getCandidate_skills().split(","));

		Map<Long, Interviewer> interviewerData = new HashMap<>();

		for (int i = 0; i < interviewersList.size(); i++) {
			if (interviewersList.get(i).getExp() > candidates.getExp()
					&& interviewersList.get(i).getStatus().equals("N")) {
				List<String> checkInterviewers = Arrays
						.asList(interviewersList.get(i).getInterview_skills().split(","));
				long stream = candidatesSkills.stream().filter(checkInterviewers::contains).count();
				interviewerData.put(stream, interviewersList.get(i));
			}
		}
		
		Set<Entry<Long,Interviewer>> entrySet = interviewerData.entrySet();
		
		List<Entry<Long,Interviewer>> list = new ArrayList<>(entrySet);
		
		Collections.sort(list,(o1, o2) -> {
			if(o2.getKey()!=o1.getKey()) {
				return o2.getKey().compareTo(o1.getKey());
			} else {
				Integer into2= o2.getValue().getExp();
				Integer into1 = o1.getValue().getExp();
				return into2.compareTo(into1);
			}
		});

		List<Interviewer> finalList = new ArrayList<Interviewer>();

		
		list.forEach(e->{
			finalList.add(e.getValue());
		});
		
		List<Interviewer> result = new ArrayList<Interviewer>();

		if (finalList.size() > 5) {
			for (int i = 0; i < 5; i++) {
				result.add(finalList.get(i));
			}
		} else {
			result = finalList;
		}

		Collections.sort(result, new ExperienceComparator());

		return result;
	}

}