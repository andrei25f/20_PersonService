package ait.cohort46.person.service;

import ait.cohort46.person.dao.PersonRepository;
import ait.cohort46.person.dto.AddressDto;
import ait.cohort46.person.dto.CityPopulationDto;
import ait.cohort46.person.dto.PersonDto;
import ait.cohort46.person.dto.exception.PersonNotFoundException;
import ait.cohort46.person.model.Address;
import ait.cohort46.person.model.Person;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Boolean addPerson(PersonDto personDto) {
        if (personRepository.existsById(personDto.getId())) {
            return false;
        }
        personRepository.save(modelMapper.map(personDto, Person.class));
        return true;
    }

    @Override
    public PersonDto findPersonById(Integer id) {
        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        return modelMapper.map(person, PersonDto.class);
    }

    @Override
    public PersonDto updatePersonName(Integer id, String name) {
        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        if (name != null) {
            person.setName(name);
            person = personRepository.save(person);
        }
        return modelMapper.map(person, PersonDto.class);
    }

    @Override
    public PersonDto updatePersonAddress(Integer id, AddressDto address) {
        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        if (address != null) {
            person.setAddress(modelMapper.map(address, Address.class));
            person = personRepository.save(person);
        }
        return modelMapper.map(person, PersonDto.class);
    }

    @Override
    public PersonDto removePerson(Integer id) {
        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        personRepository.delete(person);
        return modelMapper.map(person, PersonDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonDto[] findPersonsByName(String name) {
        return personRepository.findByNameIgnoreCase(name)
                .map(p -> modelMapper.map(p, PersonDto.class))
                .toArray(PersonDto[]::new);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonDto[] findPersonsByCity(String city) {
        return personRepository.findByAddress_CityIgnoreCase(city)
                .map(p -> modelMapper.map(p, PersonDto.class))
                .toArray(PersonDto[]::new);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonDto[] findPersonsBetweenAges(Integer minAge, Integer maxAge) {
        LocalDate dateFrom = LocalDate.now().minusYears(maxAge);
        LocalDate dateTo = LocalDate.now().minusYears(minAge);
        return personRepository.findByBirthDateBetween(dateFrom, dateTo)
                .map(p -> modelMapper.map(p, PersonDto.class))
                .toArray(PersonDto[]::new);
    }

    @Override
    public Iterable<CityPopulationDto> getCitiesPopulation(String city) {
        return null;
    }
}
