package commandhub.service.impl;

import commandhub.domain.Department;
import commandhub.repository.DepartmentRepository;
import commandhub.repository.search.DepartmentSearchRepository;
import commandhub.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link commandhub.domain.Department}.
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository departmentRepository;

    private final DepartmentSearchRepository departmentSearchRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, DepartmentSearchRepository departmentSearchRepository) {
        this.departmentRepository = departmentRepository;
        this.departmentSearchRepository = departmentSearchRepository;
    }

    @Override
    public Mono<Department> save(Department department) {
        log.debug("Request to save Department : {}", department);
        return departmentRepository.save(department).flatMap(departmentSearchRepository::save);
    }

    @Override
    public Mono<Department> update(Department department) {
        log.debug("Request to update Department : {}", department);
        return departmentRepository.save(department).flatMap(departmentSearchRepository::save);
    }

    @Override
    public Mono<Department> partialUpdate(Department department) {
        log.debug("Request to partially update Department : {}", department);

        return departmentRepository
            .findById(department.getId())
            .map(existingDepartment -> {
                if (department.getDepartmentName() != null) {
                    existingDepartment.setDepartmentName(department.getDepartmentName());
                }

                return existingDepartment;
            })
            .flatMap(departmentRepository::save)
            .flatMap(savedDepartment -> {
                departmentSearchRepository.save(savedDepartment);
                return Mono.just(savedDepartment);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Department> findAll() {
        log.debug("Request to get all Departments");
        return departmentRepository.findAll();
    }

    /**
     *  Get all the departments where JobHistory is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Department> findAllWhereJobHistoryIsNull() {
        log.debug("Request to get all departments where JobHistory is null");
        return departmentRepository.findAllWhereJobHistoryIsNull();
    }

    public Mono<Long> countAll() {
        return departmentRepository.count();
    }

    public Mono<Long> searchCount() {
        return departmentSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Department> findOne(Long id) {
        log.debug("Request to get Department : {}", id);
        return departmentRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Department : {}", id);
        return departmentRepository.deleteById(id).then(departmentSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Department> search(String query) {
        log.debug("Request to search Departments for query {}", query);
        try {
            return departmentSearchRepository.search(query);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
