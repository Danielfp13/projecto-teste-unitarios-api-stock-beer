package com.api.beer.service;

import com.api.beer.dto.BeerDTO;
import com.api.beer.entity.Beer;
import com.api.beer.exception.BeerAlreadyRegisteredException;
import com.api.beer.exception.BeerNotFoundException;
import com.api.beer.exception.BeerStockExceededException;
import com.api.beer.repository.BeerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class BeerService {

    private BeerRepository beerRepository;

    public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(beerDTO.getName());
        Beer beer = new Beer();
        BeanUtils.copyProperties(beerDTO, beer);
        Beer savedBeer = beerRepository.save(beer);
        BeanUtils.copyProperties(savedBeer, beerDTO);
        return beerDTO;

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
        BeerDTO beerDTO = new BeerDTO();
        BeanUtils.copyProperties(foundBeer, beerDTO);
        return beerDTO;
    }

    public List<BeerDTO> listAll() {
        BeerDTO c = new BeerDTO();
        return beerRepository.findAll()
                .stream()
                .map((x) -> new BeerDTO(x))
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
            BeerDTO beerDTO = new BeerDTO();
            BeanUtils.copyProperties(incrementedBeerStock, beerDTO);
            return beerDTO;
        }
        throw new BeerStockExceededException(id, quantityToIncrement);
    }
}