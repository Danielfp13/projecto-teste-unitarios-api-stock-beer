package com.api.beer.service;

import com.api.beer.dto.BeerDTO;
import com.api.beer.entity.Beer;
import com.api.beer.exception.BeerAlreadyRegisteredException;
import com.api.beer.exception.BeerNotFoundException;
import com.api.beer.repository.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BeerService {

    private final BeerRepository beerRepository;

    private final ModelMapper mapper;

    public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(beerDTO.getName());
        Beer beer = mapper.map(beerDTO, Beer.class);
        Beer savedBeer = beerRepository.save(beer);
        return mapper.map(savedBeer, BeerDTO.class);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws BeerAlreadyRegisteredException {
        Optional<Beer> optSavedBeer = beerRepository.findByName(name);
        if (optSavedBeer.isPresent()) {
            throw new BeerAlreadyRegisteredException(name);
        }
    }

    public BeerDTO findByName(String name) throws BeerNotFoundException {
        Beer foundBeer = beerRepository.findByName(name)
                .orElseThrow(() -> new BeerNotFoundException(name));
        return mapper.map(foundBeer,BeerDTO.class);
    }
}