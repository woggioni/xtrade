package com.xtrade.order.book.service;

import com.xtrade.order.book.exception.InstrumentNotFoundException;
import com.xtrade.order.book.model.Instrument;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstrumentService {
    @PersistenceContext
    private final EntityManager em;
    @Transactional
    public void createInstruments(Iterable<String> ids) {
        for (final var id : ids) {
            final var instrument = new Instrument();
            instrument.setId(id);
            em.persist(instrument);
        }
    }

    public Instrument getInstrument(String id) {
        return Optional.ofNullable(em.find(Instrument.class, id))
            .orElseThrow(
                () -> new InstrumentNotFoundException(
                    String.format(
                        "Instrument '%s' not found",
                        id
                    )
                ));
    }

    @Cacheable("all-instruments")
    public List<Instrument> getAllInstruments() {
        final var cb = em.getCriteriaBuilder();
        final var query = cb.createQuery(Instrument.class);
        final var entity = query.from(Instrument.class);
        return em.createQuery(query.select(entity)).getResultList();
    }

    @SneakyThrows
    @Transactional
    public void loadValues() {
        final var cb = em.getCriteriaBuilder();
        if(countInstruments(em, cb) == 0) {
            final var query = cb.createQuery(Long.class);
            final var entity = query.from(Instrument.class);
            query.select(cb.count(entity.as(Long.class)));
            String resourceURL = "db/instruments.txt";
            try (BufferedReader br = Optional.ofNullable(
                    getClass().getClassLoader().getResourceAsStream(resourceURL)
                )
                .map(InputStreamReader::new)
                .map(BufferedReader::new)
                .orElseThrow()) {
                br.lines().map(line -> {
                    Instrument instrument = new Instrument();
                    instrument.setId(line);
                    return instrument;
                }).forEach(em::persist);
            }
        }
    }

    private long countInstruments(EntityManager em, CriteriaBuilder cb) {
        final var query = cb.createQuery(Long.class);
        final var entity = query.from(Instrument.class);
        query.select(cb.count(entity.as(String.class)));
        return em.createQuery(query).getSingleResult();
    }

}
