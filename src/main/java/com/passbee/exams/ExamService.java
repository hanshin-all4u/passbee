package com.passbee.exams;

import com.passbee.exams.domain.Exam;
import com.passbee.exams.dto.*;
import com.passbee.exams.repo.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class ExamService {
    private final ExamRepository repo;

    public List<ExamDto> list(String q){ return repo.search(q).stream().map(this::toDto).toList(); }
    public ExamDetailDto get(Long id){
        Exam e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Exam not found: " + id));
        return toDetailDto(e);
    }

    @Transactional
    public ExamDetailDto create(ExamCreateRequest req){
        Exam e = Exam.builder()
                .title(req.title()).date(parse(req.date())).org(req.org()).link(req.link()).description(req.description()).build();
        return toDetailDto(repo.save(e));
    }

    @Transactional
    public ExamDetailDto update(Long id, ExamUpdateRequest req){
        Exam e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Exam not found: " + id));
        e.setTitle(req.title()); e.setDate(parse(req.date())); e.setOrg(req.org()); e.setLink(req.link()); e.setDescription(req.description());
        return toDetailDto(e);
    }

    @Transactional public void delete(Long id){
        if(!repo.existsById(id)) throw new IllegalArgumentException("Exam not found: " + id);
        repo.deleteById(id);
    }

    private LocalDate parse(String s){ return (s==null||s.isBlank())? null : LocalDate.parse(s); }
    private ExamDto toDto(Exam e){ return new ExamDto(e.getId(), e.getTitle(), e.getDate()==null?null:e.getDate().toString(), e.getOrg(), e.getLink()); }
    private ExamDetailDto toDetailDto(Exam e){ return new ExamDetailDto(e.getId(), e.getTitle(), e.getDate()==null?null:e.getDate().toString(), e.getOrg(), e.getLink(), e.getDescription()); }
}
