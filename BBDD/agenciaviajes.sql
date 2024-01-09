-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Jan 09, 2024 at 07:11 PM
-- Server version: 8.0.31
-- PHP Version: 8.0.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `agenciaviajes`
--

-- --------------------------------------------------------

--
-- Table structure for table `book_flight`
--

DROP TABLE IF EXISTS `book_flight`;
CREATE TABLE IF NOT EXISTS `book_flight` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `destination` varchar(255) DEFAULT NULL,
  `flight_code` varchar(255) DEFAULT NULL,
  `origin` varchar(255) DEFAULT NULL,
  `peopleq` int DEFAULT NULL,
  `price` double DEFAULT NULL,
  `seat_type` varchar(255) DEFAULT NULL,
  `id_flight` bigint DEFAULT NULL,
  `id_user` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6x4k5gtip2jvyst478vxdj91a` (`id_flight`),
  KEY `FK73i5o7p9lg7m7kqhophl9kl1u` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `book_flight`
--

INSERT INTO `book_flight` (`id`, `date`, `destination`, `flight_code`, `origin`, `peopleq`, `price`, `seat_type`, `id_flight`, `id_user`) VALUES
(1, '2024-02-15', 'Barcelona', 'MIBA-6504', 'Miami', 1, 650, 'Economy', 2, 3),
(2, '2024-02-28', 'Bogotá', 'IGBO-9657', 'Iguazú', 1, 570, 'Business', 4, 4);

-- --------------------------------------------------------

--
-- Table structure for table `book_hotel`
--

DROP TABLE IF EXISTS `book_hotel`;
CREATE TABLE IF NOT EXISTS `book_hotel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_from` date DEFAULT NULL,
  `date_to` date DEFAULT NULL,
  `hotel_code` varchar(255) DEFAULT NULL,
  `nights` int NOT NULL,
  `peopleq` int NOT NULL,
  `price` double DEFAULT NULL,
  `room_type` varchar(255) DEFAULT NULL,
  `hotel_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmdalu2ngu5lpja2llhgd5wray` (`hotel_id`),
  KEY `FKfx2q3kpi5d9691hlj9wr7spbq` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `book_hotel`
--

INSERT INTO `book_hotel` (`id`, `date_from`, `date_to`, `hotel_code`, `nights`, `peopleq`, `price`, `room_type`, `hotel_id`, `user_id`) VALUES
(1, '2024-02-10', '2024-03-19', 'RIBU-3636', 38, 1, 20634, 'Single', 3, 1),
(3, '2024-04-20', '2024-04-30', 'GRMA-4995', 10, 1, 5790, 'Doble', 5, 2);

-- --------------------------------------------------------

--
-- Table structure for table `flight`
--

DROP TABLE IF EXISTS `flight`;
CREATE TABLE IF NOT EXISTS `flight` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `destination` varchar(255) DEFAULT NULL,
  `flight_number` varchar(255) DEFAULT NULL,
  `flight_price` double DEFAULT NULL,
  `origin` varchar(255) DEFAULT NULL,
  `seat_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `flight`
--

INSERT INTO `flight` (`id`, `date`, `destination`, `flight_number`, `flight_price`, `origin`, `seat_type`) VALUES
(1, '2024-02-10', 'Miami', 'BAMI-9542', 650, 'Barcelona', 'Economy'),
(2, '2024-02-15', 'Barcelona', 'MIBA-6504', 650, 'Miami', 'Economy'),
(3, '2024-02-15', 'Iguazú', 'BOIG-7746', 570, 'Bogotá', 'Business'),
(4, '2024-02-28', 'Bogotá', 'IGBO-9657', 570, 'Iguazú', 'Business'),
(5, '2024-03-15', 'Madrid', 'MIMA-1129', 1000, 'Miami', 'Economy'),
(7, '2024-02-10', 'Miami', 'BAMI-5470', 4320, 'Barcelona', 'Business');

-- --------------------------------------------------------

--
-- Table structure for table `hotel`
--

DROP TABLE IF EXISTS `hotel`;
CREATE TABLE IF NOT EXISTS `hotel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `hotel_code` varchar(255) DEFAULT NULL,
  `is_booked` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `place` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `hotel`
--

INSERT INTO `hotel` (`id`, `hotel_code`, `is_booked`, `name`, `place`) VALUES
(1, 'ATMI-7717', b'0', 'Atlantis Resort', 'Miami'),
(2, 'ATMI-1394', b'0', 'Atlantis Resort 2', 'Miami'),
(3, 'RIBU-3636', b'1', 'Ritz Carlton', 'Buenos Aires'),
(4, 'RIME-1919', b'0', 'Ritz Carlton 2', 'Medellín'),
(5, 'GRMA-4995', b'1', 'Grand Hyatt', 'Madrid');

-- --------------------------------------------------------

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
CREATE TABLE IF NOT EXISTS `room` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `disponibility_date_from` date DEFAULT NULL,
  `disponibility_date_to` date DEFAULT NULL,
  `room_price` double DEFAULT NULL,
  `room_type` varchar(255) DEFAULT NULL,
  `hotel_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdosq3ww4h9m2osim6o0lugng8` (`hotel_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `room`
--

INSERT INTO `room` (`id`, `disponibility_date_from`, `disponibility_date_to`, `room_price`, `room_type`, `hotel_id`) VALUES
(1, '2024-02-10', '2024-03-20', 630, 'Doble', 1),
(2, '2024-02-10', '2024-03-23', 820, 'Triple', 2),
(3, '2024-02-10', '2024-03-19', 543, 'Single', 3),
(4, '2024-02-12', '2024-04-17', 720, 'Doble', 4),
(5, '2024-04-17', '2024-05-23', 579, 'Doble', 5);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `age` int NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `pass_port` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `age`, `email`, `last_name`, `name`, `pass_port`) VALUES
(1, 34, 'antonio@prueba.es', 'Perez Lopez', 'Antonio', 'AB123456A'),
(2, 65, 'ramon@prueba.es', 'Jurado perez', 'Ramon', 'XY123456Z'),
(3, 25, 'Gonzalo@prueba.es', 'Marin perez', 'Gonzalo', 'BA654321B'),
(4, 45, 'tomas@prueba.es', 'Bonilla garcia', 'Tomas', 'YZ597642J'),
(5, 45, 'torij@prueba.es', 'Bonilla garcia', 'torij', 'YZ597642J');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `book_flight`
--
ALTER TABLE `book_flight`
  ADD CONSTRAINT `FK6x4k5gtip2jvyst478vxdj91a` FOREIGN KEY (`id_flight`) REFERENCES `flight` (`id`),
  ADD CONSTRAINT `FK73i5o7p9lg7m7kqhophl9kl1u` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`);

--
-- Constraints for table `book_hotel`
--
ALTER TABLE `book_hotel`
  ADD CONSTRAINT `FKfx2q3kpi5d9691hlj9wr7spbq` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKmdalu2ngu5lpja2llhgd5wray` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`);

--
-- Constraints for table `room`
--
ALTER TABLE `room`
  ADD CONSTRAINT `FKdosq3ww4h9m2osim6o0lugng8` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
