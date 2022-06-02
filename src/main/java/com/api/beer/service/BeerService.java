package com.api.beer.service;

import com.api.beer.dto.BeerDTO;
import com.api.beer.entity.Beer;
import com.api.beer.exception.BeerAlreadyRegisteredException;
import com.api.beer.exception.BeerNotFoundException;
import com.api.beer.exception.BeerStockExceededException;
import com.api.beer.repository.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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

    public List<BeerDTO> listAll() {
        return beerRepository.findAll()
                .stream()
                .map( (x) -> mapper.map(x, BeerDTO.class))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws BeerNotFoundException {
        verifyIfExists(id);
        beerRepository.deleteById(id);
    }

    private Beer verifyIfExists(Long id) throws BeerNotFoundException {
        return beerRepository.findById(id)
                .orElseThrow(() -> new BeerNotFoundException(id));
    }

    public BeerDTO increment(Long id, int quantityToIncrement) throws BeerNotFoundException, BeerStockExceededException {
        Beer beerToIncrementStock = verifyIfExists(id);
        int quantityAfterIncrement = quantityToIncrement + beerToIncrementStock.getQuantity();
        if (quantityAfterIncrement <= beerToIncrementStock.getMax()) {
            beerToIncrementStock.setQuantity(beerToIncrementStock.getQuantity() + quantityToIncrement);
            Beer incrementedBeerStock = beerRepository.save(beerToIncrementStock);
            return mapper.map(incrementedBeerStock, BeerDTO.class);
        }
        throw new BeerStockExceededException(id, quantityToIncrement);
    }
}