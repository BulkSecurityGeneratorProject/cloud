package cloud.service.impl;

import cloud.service.SessionService;
import cloud.domain.Session;
import cloud.repository.SessionRepository;
import cloud.service.dto.SessionDTO;
import cloud.service.mapper.SessionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing Session.
 */
@Service
@Transactional
public class SessionServiceImpl implements SessionService {

    private final Logger log = LoggerFactory.getLogger(SessionServiceImpl.class);

    private final SessionRepository sessionRepository;

    private final SessionMapper sessionMapper;

    public SessionServiceImpl(SessionRepository sessionRepository, SessionMapper sessionMapper) {
        this.sessionRepository = sessionRepository;
        this.sessionMapper = sessionMapper;
    }

    /**
     * Save a session.
     *
     * @param sessionDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public SessionDTO save(SessionDTO sessionDTO) {
        log.debug("Request to save Session : {}", sessionDTO);
        Session session = sessionMapper.toEntity(sessionDTO);
        session = sessionRepository.save(session);
        return sessionMapper.toDto(session);
    }

    /**
     * Get all the sessions.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SessionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sessions");
        return sessionRepository.findAll(pageable)
            .map(sessionMapper::toDto);
    }

    /**
     * Get one session by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public SessionDTO findOne(Long id) {
        log.debug("Request to get Session : {}", id);
        Session session = sessionRepository.findOne(id);
        return sessionMapper.toDto(session);
    }

    /**
     * Delete the session by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Session : {}", id);
        sessionRepository.delete(id);
    }
}
