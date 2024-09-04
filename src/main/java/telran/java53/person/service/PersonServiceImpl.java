package telran.java53.person.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import telran.java53.person.dao.PersonRepository;
import telran.java53.person.dto.AddressDto;
import telran.java53.person.dto.CityPopulationDto;
import telran.java53.person.dto.PersonDto;
import telran.java53.person.dto.exception.PersonNotFoundException;
import telran.java53.person.model.Address;
import telran.java53.person.model.Person;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
	final PersonRepository personRepository;
	final ModelMapper modelMapper;

	@Transactional
	@Override
	public Boolean addPerson(PersonDto personDto) {
		if(personRepository.existsById(personDto.getId())) {
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
	public PersonDto removePerson(Integer id) {
		Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
		personRepository.delete(person);
		return modelMapper.map(person, PersonDto.class);
	}

	@Override
	public PersonDto updatePersonName(Integer id, String name) {
		Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);	
		person.setName(name);
		personRepository.save(person);
		return modelMapper.map(person, PersonDto.class);
	}

	@Override
	public PersonDto updatePersonAddress(Integer id, AddressDto addressDto) {
		Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);	
		Address address = modelMapper.map(addressDto, Address.class);
		person.setAddress(address);
		personRepository.save(person);
		return modelMapper.map(person, PersonDto.class);
	}

	@Override
	public PersonDto[] findPersonsByCity(String city) {
		List<Person> allPersons = personRepository.findAll();
		List<PersonDto> filteredPersons = allPersons.stream()
		            .filter(person -> person.getAddress() != null && city.equals(person.getAddress().getCity()))
		            .map(person -> modelMapper.map(person, PersonDto.class))
		            .collect(Collectors.toList());
		return filteredPersons.toArray(new PersonDto[0]);
	}

	@Override
	public PersonDto[] findPersonsByName(String name) {
		List<Person> allPersons = personRepository.findAll();
		List<PersonDto> filteredPersons = allPersons.stream()
				    .filter(person -> person.getName() != null && name.equals(person.getName()))
				    .map(person -> modelMapper.map(person, PersonDto.class))
				    .collect(Collectors.toList());
		return filteredPersons.toArray(new PersonDto[0]);
	}

	@Override
	public PersonDto[] findPersonsBetweenAge(Integer minAge, Integer maxAge) {
		List<Person> allPersons = personRepository.findAll();
		LocalDate currentDate = LocalDate.now();
		List<PersonDto> filteredPersons = allPersons.stream()
	            .filter(person -> {
	                int age = calculateAge(person.getBirthDate(), currentDate);
	                return age >= minAge && age <= maxAge;
	            })
	            .map(person -> modelMapper.map(person, PersonDto.class))
	            .collect(Collectors.toList());
		return filteredPersons.toArray(new PersonDto[0]);
	}

	private int calculateAge(LocalDate birthDate, LocalDate currentDate) {
		if (birthDate != null && currentDate != null) {
	        return Period.between(birthDate, currentDate).getYears();
	    } else {
	        return 0;
	    }
	}

	@Override
	public Iterable<CityPopulationDto> getCityPopulation() {
		// TODO Auto-generated method stub
		return null;
	}

}
