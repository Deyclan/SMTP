-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Client :  127.0.0.1
-- Généré le :  Sam 24 Mars 2018 à 17:11
-- Version du serveur :  10.1.21-MariaDB
-- Version de PHP :  5.6.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `ipc`
--

-- --------------------------------------------------------

--
-- Structure de la table `mail`
--

CREATE TABLE `mail` (
  `id` int(50) NOT NULL,
  `mailUser` varchar(100) NOT NULL,
  `expediteur` varchar(200) NOT NULL,
  `destinataire` varchar(200) NOT NULL,
  `subject` varchar(200) NOT NULL,
  `dateMail` varchar(200) NOT NULL,
  `message_id` varchar(200) NOT NULL,
  `content` varchar(2000) NOT NULL,
  `num` int(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `mail`
--

INSERT INTO `mail` (`id`, `mailUser`, `expediteur`, `destinataire`, `subject`, `dateMail`, `message_id`, `content`, `num`) VALUES
(1, 'philippe@ait.com', 'julien@ait.com', 'philippe@ait.com', 'Cachette', 'Fri, 23 Nov 1997 09:55:06 -0600', '<p1@ait.com>', 'Je sais où tu te caches.', 1),
(2, 'philippe@ait.com', 'julien@ait.com', 'philippe@ait.com', 'Pognon', 'Fri, 24 Nov 1997 09:55:06 -0600', '<p2@ait.com>', 'Tous les deux on peut se faire du fric.', 2),
(3, 'philippe@ait.com', 'julien@ait.com', 'philippe@ait.com', 'Noël', 'Fri, 25 Dec 2017 09:50:06 -0600', '<p3@ait.com>', 'Les boules de Noël.', 3),
(4, 'monfrere@gstaad.fr', 'julien@ait.com', 'monfrere@gstaad.fr', 'Chinois', 'Fri, 25 Nov 1997 09:55:06 -0600', '<m1@gstaad.fr>', 'Tu as tué mon frère à Gstaad', 1),
(5, 'monfrere@gstaad.fr', 'julien@ait.com', 'monfrere@gstaad.fr', 'Menace', 'Fri, 26 Nov 1997 09:55:06 -0600', '<m2@gstaad.fr>', 'Tu vas mourir', 2),
(6, 'monfrere@gstaad.fr', 'julien@ait.com', 'monfrere@gstaad.fr', 'Bye', 'Fri, 29 Nov 1997 09:55:06 -0600', '<m3@gstaad.fr>', 'Hasta la vista bye-bye.', 3);

-- --------------------------------------------------------

--
-- Structure de la table `serveur`
--

CREATE TABLE `serveur` (
  `nom` varchar(50) NOT NULL,
  `adresseMail` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `serveur`
--

INSERT INTO `serveur` (`nom`, `adresseMail`) VALUES
('ait.com', 'julien@ait.com'),
('ait.com', 'philippe@ait.com'),
('gstaad.fr', 'monfrere@gstaad.fr');

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE `user` (
  `nom` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `mail` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `user`
--

INSERT INTO `user` (`nom`, `password`, `mail`) VALUES
('Monfrere', 'Gstaad', 'monfrere@gstaad.fr'),
('Philippe', 'Fric', 'philippe@ait.com');

--
-- Index pour les tables exportées
--

--
-- Index pour la table `mail`
--
ALTER TABLE `mail`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `serveur`
--
ALTER TABLE `serveur`
  ADD PRIMARY KEY (`nom`,`adresseMail`);

--
-- Index pour la table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`nom`);

--
-- AUTO_INCREMENT pour les tables exportées
--

--
-- AUTO_INCREMENT pour la table `mail`
--
ALTER TABLE `mail`
  MODIFY `id` int(50) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
